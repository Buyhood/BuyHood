package api.buyhood.product.service;

import api.buyhood.dto.productcategory.ProductCategoryFeignDto;
import api.buyhood.dto.store.StoreFeignDto;
import api.buyhood.dto.user.UserFeignDto;
import api.buyhood.errorcode.CategoryErrorCode;
import api.buyhood.errorcode.CommonErrorCode;
import api.buyhood.errorcode.ProductErrorCode;
import api.buyhood.errorcode.StoreErrorCode;
import api.buyhood.errorcode.UserErrorCode;
import api.buyhood.exception.ConflictException;
import api.buyhood.exception.InvalidRequestException;
import api.buyhood.exception.NotFoundException;
import api.buyhood.exception.ServerException;
import api.buyhood.product.client.ProductCategoryFeignClient;
import api.buyhood.product.client.StoreFeignClient;
import api.buyhood.product.client.UserFeignClient;
import api.buyhood.product.dto.response.RegisterProductRes;
import api.buyhood.product.entity.Product;
import api.buyhood.product.entity.ProductCategoryMapping;
import api.buyhood.product.repository.ProductCategoryMappingRepository;
import api.buyhood.product.repository.ProductRepository;
import feign.FeignException;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import static api.buyhood.errorcode.ProductErrorCode.PRODUCT_NOT_FOUND;
import static api.buyhood.errorcode.StoreErrorCode.NOT_STORE_OWNER;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;
	private final ProductCategoryMappingRepository productCategoryMappingRepository;
	private final StoreFeignClient storeFeignClient;
	private final ProductCategoryFeignClient productCategoryFeignClient;
	private final UserFeignClient userFeignClient;

	@Transactional
	public RegisterProductRes registerProduct(
		Long currentUserId,
		Long storeId,
		String productName,
		Long price,
		Long stock,
		List<Long> categoryIds,
		String description
	) {
		StoreFeignDto getStoreDto = fetchStore(storeId);
		UserFeignDto getUserDto = fetchUser(currentUserId);

		if (!getUserDto.getId().equals(getStoreDto.getSellerId())) {
			throw new InvalidRequestException(NOT_STORE_OWNER);
		}

		List<Product> getProducts = productRepository.findActiveProductsByStoreId(getStoreDto.getStoreId());
		checkForDuplicateInStore(productName, getProducts);

		Product newProduct = Product.builder()
			.name(productName)
			.price(price)
			.stock(stock)
			.description(description)
			.storeId(storeId)
			.build();

		productRepository.save(newProduct);

		List<String> categoryNames = new ArrayList<>();
		try {
			for (Long categoryId : categoryIds) {
				ProductCategoryFeignDto getProductCategoryDto =
					productCategoryFeignClient.getCategoryOrElseThrow(categoryId);

				categoryNames.add(getProductCategoryDto.getCategoryName());
			}
		} catch (FeignException e) {
			log.error("[FeignException]: 상품에 적용하려는 상품 카테고리가 실제로 존재하지 않음. request: {}, contentUTF8: {}\n",
				e.request(), e.contentUTF8(), e);
			throw new NotFoundException(CategoryErrorCode.CATEGORY_NOT_FOUND);
		}

		List<ProductCategoryMapping> mappings = categoryIds.stream()
			.map(categoryId ->
				ProductCategoryMapping.builder()
					.categoryId(categoryId)
					.productId(newProduct.getId())
					.build()
			)
			.toList();

		productCategoryMappingRepository.saveAll(mappings);

		return RegisterProductRes.of(newProduct, categoryNames);
	}

	@Transactional
	public void patchProduct(
		Long currentUserId,
		Long storeId,
		Long productId,
		String productName,
		Long price,
		Long stock,
		List<Long> categoryIds,
		String description
	) {

		UserFeignDto getUserDto = fetchUser(currentUserId);
		StoreFeignDto getStoreDto = fetchStore(storeId);

		if (!getUserDto.getId().equals(getStoreDto.getSellerId())) {
			throw new InvalidRequestException(NOT_STORE_OWNER);
		}

		Product product = getProductOrElseThrow(productId);

		if (StringUtils.hasText(productName) && !productName.equals(product.getName())) {
			List<Product> getProducts = productRepository.findActiveProductsByStoreId(storeId);
			checkForDuplicateInStore(productName, getProducts);
			product.patchName(productName);
		}

		if (price != null) {
			product.patchPrice(price);
		}

		if (stock != null) {
			product.patchStock(stock);
		}

		if (StringUtils.hasText(description)) {
			product.patchDescription(description);
		}

		if (categoryIds != null && !categoryIds.isEmpty()) {
			// 요청으로 받은 카테고리 ID 값이 실제로 존재하는지 확인
			categoryIds.forEach(this::validateCategoryExists);

			// 매핑 테이블에 저장된 내용 삭제
			productCategoryMappingRepository.deleteByProductId(productId);

			// 새로운 카테고리 ID로 등록
			List<ProductCategoryMapping> newMappings = categoryIds.stream()
				.map(categoryId -> ProductCategoryMapping.builder()
					.productId(productId)
					.categoryId(categoryId)
					.build())
				.toList();

			productCategoryMappingRepository.saveAll(newMappings);
		}
	}

	@Transactional
	public void addCategoriesToProduct(Long productId, List<Long> categoryIds) {
		if (!productRepository.existsActiveProductById(productId)) {
			throw new NotFoundException(PRODUCT_NOT_FOUND);
		}

		for (Long categoryId : categoryIds) {
			validateCategoryExists(categoryId);

			boolean exists = productCategoryMappingRepository.existsByProductIdAndCategoryId(productId, categoryId);
			if (!exists) {
				ProductCategoryMapping mapping = ProductCategoryMapping.builder()
					.productId(productId)
					.categoryId(categoryId)
					.build();
				productCategoryMappingRepository.save(mapping);
			}
		}
	}

	@Transactional
	public void removeCategoriesFromProduct(Long productId, List<Long> categoryIds) {
		if (!productRepository.existsActiveProductById(productId)) {
			throw new NotFoundException(PRODUCT_NOT_FOUND);
		}

		for (Long categoryId : categoryIds) {
			boolean exists = productCategoryMappingRepository.existsByProductIdAndCategoryId(productId, categoryId);
			if (exists) {
				productCategoryMappingRepository.deleteByProductIdAndCategoryId(productId, categoryId);
			}
		}
	}

	private Product getProductOrElseThrow(Long productId) {
		return productRepository.findActiveProductByProductId(productId)
			.orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));
	}

	private void checkForDuplicateInStore(String productName, List<Product> products) {
		for (Product product : products) {
			if (product.getName().equalsIgnoreCase(productName)) {
				throw new ConflictException(ProductErrorCode.DUPLICATE_PRODUCT_NAME);
			}
		}
	}

	private UserFeignDto fetchUser(Long userId) {
		try {
			return userFeignClient.getRoleSellerOrElseThrow(userId);
		} catch (FeignException e) {
			if (e.status() == 404) {
				throw new NotFoundException(UserErrorCode.USER_NOT_FOUND);
			}
			throw new ServerException(CommonErrorCode.NOT_DEFINED_FEIGN_EXCEPTION);
		}
	}

	private StoreFeignDto fetchStore(Long storeId) {
		try {
			return storeFeignClient.getStoreOrElseThrow(storeId);
		} catch (FeignException e) {
			if (e.status() == 404) {
				throw new NotFoundException(StoreErrorCode.STORE_NOT_FOUND);
			}
			throw new ServerException(CommonErrorCode.NOT_DEFINED_FEIGN_EXCEPTION);
		}
	}

	private void validateCategoryExists(Long categoryId) {
		try {
			productCategoryFeignClient.getCategoryOrElseThrow(categoryId);
		} catch (FeignException e) {
			if (e.status() == 404) {
				throw new NotFoundException(CategoryErrorCode.CATEGORY_NOT_FOUND);
			}
			throw new ServerException(CommonErrorCode.NOT_DEFINED_FEIGN_EXCEPTION);
		}
	}

	//혹시나 충돌이슈있을까봐 밑에 추가합니다
	@Transactional
	public void deleteProduct(Long currentUserId, Long storeId, Long productId) {
		StoreFeignDto getStoreDto = fetchStore(storeId);
		UserFeignDto getUserDto = fetchUser(currentUserId);
		//제품을 삭제할 유저가 가게 주인장인지?
		if (!getStoreDto.getSellerId().equals(getUserDto.getId())) {
			throw new InvalidRequestException(NOT_STORE_OWNER);
		}
		//제품이 active 상태인지?
		Product getProduct = getProductOrElseThrow(productId);

		getProduct.markDeleted();
	}
}
