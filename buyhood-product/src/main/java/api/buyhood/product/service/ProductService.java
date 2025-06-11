package api.buyhood.product.service;

import api.buyhood.dto.productcategory.ProductCategoryFeignDto;
import api.buyhood.dto.store.StoreFeignDto;
import api.buyhood.dto.user.UserFeignDto;
import api.buyhood.errorcode.*;
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
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static api.buyhood.errorcode.ProductErrorCode.OUT_OF_STOCK;
import static api.buyhood.errorcode.ProductErrorCode.PRODUCT_NOT_FOUND;

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
		Long storeId,
		String productName,
		Long price,
		Long stock,
		List<Long> categoryIds,
		String description
	) {
		StoreFeignDto getStoreDto = null;

		try {
			getStoreDto = storeFeignClient.getStoreOrElseThrow(storeId);
		} catch (FeignException e) {
			log.error("[FeignException]: 가게가 실제로 존재하지 않음. request: {}, contentUTF8: {}\n",
				e.request(), e.contentUTF8(), e);
			throw new NotFoundException(StoreErrorCode.STORE_NOT_FOUND);
		}

		List<Product> getProducts = productRepository.findActiveProductsByStoreId(getStoreDto.getStoreId());
		checkForDuplicateInStore(productName, getProducts);

		Product newProduct = Product.builder()
			.name(productName)
			.price(price)
			.stock(stock)
			.description(description)
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

		if (!getUserDto.getUserId().equals(getStoreDto.getSellerId())) {
			throw new InvalidRequestException(StoreErrorCode.NOT_STORE_OWNER);
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
			throw new NotFoundException(ProductErrorCode.PRODUCT_NOT_FOUND);
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
			throw new NotFoundException(ProductErrorCode.PRODUCT_NOT_FOUND);
		}

		for (Long categoryId : categoryIds) {
			boolean exists = productCategoryMappingRepository.existsByProductIdAndCategoryId(productId, categoryId);
			if (exists) {
				productCategoryMappingRepository.deleteByProductIdAndCategoryId(productId, categoryId);
			}
		}
	}

	@Retryable(
			retryFor = {
					OptimisticLockException.class,
					ObjectOptimisticLockingFailureException.class
			},
			backoff = @Backoff(delay = 100)
	)
	@Transactional
	public void decreaseStock(List<Long> productIdList, List<Integer> quantityList, Map<Long, Product> productMap) {

		for (int i = 0; i < productIdList.size(); i++) {
			Long productId = productIdList.get(i);
			Integer quantity = quantityList.get(i);

			Product product = productMap.get(productId);

			if (product.getStock() < quantity) {
				throw new InvalidRequestException(OUT_OF_STOCK);
			}

			product.decreaseStock(quantity);
		}
	}

	@Retryable(
			retryFor = {
					OptimisticLockException.class,
					ObjectOptimisticLockingFailureException.class
			},
			backoff = @Backoff(delay = 100)
	)
	@Transactional
	public void rollBackStock(Map<Long, Integer> orderHistories) {
		for (Map.Entry<Long, Integer> entry : orderHistories.entrySet()) {
			Long productId = entry.getKey();
			Integer quantity = entry.getValue();

			Product product = productRepository.findById(productId)
					.orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));

			product.rollBackStock(quantity);
		}
	}

	/* 주문 요청 후 재고 감소에 대한 recover */
	@Recover
	public void recover(OptimisticLockException e, List<Long> productIdList, List<Integer> quantityList, Map<Long, Product> productMap) {
		throw new InvalidRequestException(ProductErrorCode.STOCK_UPDATE_CONFLICT);
	}

	@Recover
	public void recover(ObjectOptimisticLockingFailureException e, List<Long> productIdList, List<Integer> quantityList, Map<Long, Product> productMap) {
		throw new InvalidRequestException(ProductErrorCode.STOCK_UPDATE_CONFLICT);
	}

	/* 주문 취소 후 재고 롤백에 대한 recover*/
	@Recover
	public void recover(OptimisticLockException e, Map<Long, Integer> orderHistories) {
		throw new InvalidRequestException(ProductErrorCode.STOCK_UPDATE_CONFLICT);
	}

	@Recover
	public void recover(ObjectOptimisticLockingFailureException e, Map<Long, Integer> orderHistories) {
		throw new InvalidRequestException(ProductErrorCode.STOCK_UPDATE_CONFLICT);
	}


	private Product getProductOrElseThrow(Long productId) {
		return productRepository.findActiveProductByProductId(productId)
			.orElseThrow(() -> new NotFoundException(ProductErrorCode.PRODUCT_NOT_FOUND));
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
}
