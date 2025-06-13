package api.buyhood.product.service;

import api.buyhood.dto.store.StoreFeignDto;
import api.buyhood.dto.user.UserFeignDto;
import api.buyhood.errorcode.CategoryErrorCode;
import api.buyhood.errorcode.CommonErrorCode;
import api.buyhood.errorcode.StoreErrorCode;
import api.buyhood.errorcode.UserErrorCode;
import api.buyhood.exception.InvalidRequestException;
import api.buyhood.exception.NotFoundException;
import api.buyhood.exception.ServerException;
import api.buyhood.product.client.ProductCategoryFeignClient;
import api.buyhood.product.client.StoreFeignClient;
import api.buyhood.product.client.UserFeignClient;
import api.buyhood.product.dto.response.GetProductRes;
import api.buyhood.product.dto.response.PageProductRes;
import api.buyhood.product.entity.Product;
import api.buyhood.product.entity.ProductCategoryMapping;
import api.buyhood.product.repository.ProductCategoryMappingRepository;
import api.buyhood.product.repository.ProductRepository;
import feign.FeignException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static api.buyhood.errorcode.ProductErrorCode.PRODUCT_NOT_FOUND;
import static api.buyhood.errorcode.StoreErrorCode.NOT_STORE_OWNER;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductQueryService {

	private final ProductRepository productRepository;
	private final ProductCategoryMappingRepository productCategoryMappingRepository;
	private final StoreFeignClient storeFeignClient;
	private final ProductCategoryFeignClient productCategoryFeignClient;
	private final UserFeignClient userFeignClient;

	// 단건 조회
	public GetProductRes getProduct(Long userId, Long storeId, Long productId) {
		//가게가 존재하는지 조회
		StoreFeignDto getStoreDto = fetchStore(storeId);
		UserFeignDto getUserDto = fetchUser(userId);

		//본인 가게만 조회할 수 있음.
		if (!getUserDto.getId().equals(getStoreDto.getSellerId())) {
			throw new InvalidRequestException(NOT_STORE_OWNER);
		}

		// 상품 존재 여부 조회
		Product product = productRepository.findActiveProductByProductId(productId)
			.orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));

		// 카테고리 매핑 조회
		List<ProductCategoryMapping> mappings = productCategoryMappingRepository.findByProductId(productId);

		List<Long> categoryIds = mappings.stream()
			.map(ProductCategoryMapping::getCategoryId)
			.toList();

		List<String> categoryNames = categoryIds.stream()
			.map(id -> productCategoryFeignClient.getCategoryOrElseThrow(id).getCategoryName())
			.toList();

		return GetProductRes.of(product, categoryNames);

	}

	/**
	 * 상품 전체 페이징 조회
	 *
	 * @param storeId  가게 ID (필수)
	 * @param pageable
	 * @author dereck-jun
	 */
	@Transactional(readOnly = true)
	public Page<PageProductRes> getAllProducts(Long storeId, Pageable pageable) {
		StoreFeignDto getStoreDto = fetchStore(storeId);

		PageRequest pageRequest =
			PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Direction.ASC, "name");

		Page<Product> productPage = productRepository.findActiveProductsByStoreIdWithPaging(storeId, pageRequest);

		List<Long> productIds = productPage.getContent()
			.stream()
			.map(Product::getId)
			.toList();

		Map<Long, List<String>> productCategoryNameMap = getProductCategoryNameMap(productIds);

		return PageProductRes.of(productPage, productCategoryNameMap);
	}
//
//	/**
//	 * 상품 키워드 조회
//	 *
//	 * @param storeId  가게 ID (필수)
//	 * @param keyword  상품 이름에 대한 키워드 (선택)
//	 * @param pageable
//	 * @author dereck-jun
//	 */
//	@Transactional(readOnly = true)
//	public Page<PageProductRes> getProductByKeyword(Long storeId, String keyword, Pageable pageable) {
//		if (!storeRepository.existsActiveStoreById(storeId)) {
//			throw new NotFoundException(StoreErrorCode.STORE_NOT_FOUND);
//		}
//
//		PageRequest pageRequest =
//			PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Direction.ASC, "name");
//
//		Page<Product> productPage =
//			productRepository.findActiveProductsByStoreIdAndKeyword(storeId, keyword, pageRequest);
//
//		List<Long> productIds = productPage.getContent()
//			.stream()
//			.map(Product::getId)
//			.toList();
//
//		Map<Long, List<String>> productCategoryNameMap = getProductCategoryNameMap(productIds);
//
//		return PageProductRes.of(productPage, productCategoryNameMap);
//	}

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

	private Map<Long, List<String>> getProductCategoryNameMap(List<Long> productIds) {
		// 1. 상품-카테고리 매핑 한 번에 조회
		List<ProductCategoryMapping> mappings = productCategoryMappingRepository.findByProductIdIn(productIds);

		// 2. 상품별로 카테고리 ID 그룹화
		Map<Long, List<Long>> productCategoryIdsMap = mappings.stream()
			.collect(Collectors.groupingBy(
				ProductCategoryMapping::getProductId,
				Collectors.mapping(ProductCategoryMapping::getCategoryId, Collectors.toList())
			));

		// 3. 모든 카테고리 ID에 대해 이름 조회 (FeignClient 등)
		Map<Long, String> categoryIdToName = productCategoryIdsMap.values().stream()
			.flatMap(List::stream)
			.distinct()
			.collect(Collectors.toMap(
				id -> id,
				id -> productCategoryFeignClient.getCategoryOrElseThrow(id).getCategoryName()
			));

		// 4. 상품별로 카테고리명 리스트 매핑
		Map<Long, List<String>> result = new HashMap<>();
		for (Map.Entry<Long, List<Long>> entry : productCategoryIdsMap.entrySet()) {
			List<String> categoryNames = entry.getValue().stream()
				.map(categoryIdToName::get)
				.toList();
			result.put(entry.getKey(), categoryNames);
		}
		return result;
	}
}
