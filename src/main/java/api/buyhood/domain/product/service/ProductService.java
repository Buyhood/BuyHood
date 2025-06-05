package api.buyhood.domain.product.service;

import api.buyhood.domain.cart.entity.Cart;
import api.buyhood.domain.cart.entity.CartItem;
import api.buyhood.domain.product.dto.response.GetProductRes;
import api.buyhood.domain.product.dto.response.PageProductRes;
import api.buyhood.domain.product.dto.response.RegisterProductRes;
import api.buyhood.domain.product.entity.Category;
import api.buyhood.domain.product.entity.Product;
import api.buyhood.domain.product.entity.ProductCategory;
import api.buyhood.domain.product.repository.CategoryRepository;
import api.buyhood.domain.product.repository.ProductCategoryRepository;
import api.buyhood.domain.product.repository.ProductRepository;
import api.buyhood.domain.store.entity.Store;
import api.buyhood.domain.store.repository.StoreRepository;
import api.errorcode.CategoryErrorCode;
import api.errorcode.ProductErrorCode;
import api.errorcode.StoreErrorCode;
import api.exception.ConflictException;
import api.exception.ForbiddenException;
import api.exception.InvalidRequestException;
import api.exception.NotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;
	private final ProductCategoryRepository productCategoryRepository;
	private final StoreRepository storeRepository;

	/**
	 * 상품 등록
	 *
	 * @param currentUserId  로그인한 사용자 ID (필수)
	 * @param storeId        가게 ID (필수)
	 * @param productName    상품 이름 (필수)
	 * @param price          상품 가격 (필수)
	 * @param stock          상품 개수 (필수)
	 * @param categoryIdList 상품 카테고리 (선택)
	 * @param description    상품 설명 (선택)
	 * @author dereck-jun
	 */
	@Transactional
	public RegisterProductRes registerProduct(
		Long currentUserId,
		Long storeId,
		String productName,
		Long price,
		Long stock,
		List<Long> categoryIdList,
		String description
	) {
		Store getStore = storeRepository.findById(storeId)
			.orElseThrow(() -> new NotFoundException(StoreErrorCode.STORE_NOT_FOUND));

		if (!getStore.getSeller().getId().equals(currentUserId)) {
			throw new ForbiddenException(StoreErrorCode.NOT_STORE_OWNER);
		}

		if (productRepository.existsByStoreIdAndProductName(storeId, productName)) {
			throw new ConflictException(ProductErrorCode.DUPLICATE_PRODUCT_NAME);
		}

		Product product = Product.builder()
			.name(productName)
			.price(price)
			.description(description)
			.stock(stock)
			.store(getStore)
			.build();

		productRepository.save(product);

		// 중간 테이블 매핑
		if (categoryIdList != null && !categoryIdList.isEmpty()) {
			linkCategoriesToProduct(categoryIdList, product);
		}

		List<String> categoryNameList = categoryRepository.findCategoryNamesByCategoryIds(categoryIdList);

		return RegisterProductRes.of(product, categoryNameList);
	}

	/**
	 * 상품 단건 조회
	 *
	 * @param storeId   가게 ID (필수)
	 * @param productId 상품 ID (필수)
	 * @author dereck-jun
	 */
	@Transactional(readOnly = true)
	public GetProductRes getProduct(Long storeId, Long productId) {
		if (!storeRepository.existsActiveStoreById(storeId)) {
			throw new NotFoundException(StoreErrorCode.STORE_NOT_FOUND);
		}

		// 상품 존재 여부 조회
		Product product = productRepository.findActiveProductByStoreIdAndProductId(storeId, productId)
			.orElseThrow(() -> new NotFoundException(ProductErrorCode.PRODUCT_NOT_FOUND));

		// 중간 엔티티에서 카테고리 id 목록 조회
		List<Long> categoryIds = productCategoryRepository.findCategoryIdsByProductId(productId);

		// 카테고리 이름 목록 조회
		List<String> categoryNames = categoryRepository.findCategoryNamesByCategoryIds(categoryIds);

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
		if (!storeRepository.existsActiveStoreById(storeId)) {
			throw new NotFoundException(StoreErrorCode.STORE_NOT_FOUND);
		}

		PageRequest pageRequest =
			PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Direction.ASC, "name");

		Page<Product> productPage = productRepository.findActiveProductsByStoreId(storeId, pageRequest);

		List<Long> productIds = productPage.getContent()
			.stream()
			.map(Product::getId)
			.toList();

		Map<Long, List<String>> productCategoryNameMap = getProductCategoryNameMap(productIds);

		return PageProductRes.of(productPage, productCategoryNameMap);
	}

	/**
	 * 상품 키워드 조회
	 *
	 * @param storeId  가게 ID (필수)
	 * @param keyword  상품 이름에 대한 키워드 (선택)
	 * @param pageable
	 * @author dereck-jun
	 */
	@Transactional(readOnly = true)
	public Page<PageProductRes> getProductByKeyword(Long storeId, String keyword, Pageable pageable) {
		if (!storeRepository.existsActiveStoreById(storeId)) {
			throw new NotFoundException(StoreErrorCode.STORE_NOT_FOUND);
		}

		PageRequest pageRequest =
			PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Direction.ASC, "name");

		Page<Product> productPage =
			productRepository.findActiveProductsByStoreIdAndKeyword(storeId, keyword, pageRequest);

		List<Long> productIds = productPage.getContent()
			.stream()
			.map(Product::getId)
			.toList();

		Map<Long, List<String>> productCategoryNameMap = getProductCategoryNameMap(productIds);

		return PageProductRes.of(productPage, productCategoryNameMap);
	}

	/**
	 * 상품 수정
	 *
	 * @param currentUserId  로그인한 사용자 ID (필수)
	 * @param storeId        가게 ID (필수)
	 * @param productId      수정할 상품의 ID (필수)
	 * @param productName    변경하려고 하는 상품 이름 (선택)
	 * @param price          변경하려고 하는 상품 가격 (선택)
	 * @param categoryIdList 연결 또는 연결 해제할 categoryIdList (선택)
	 * @param description    변경하려고 하는 상품 설명 (선택)
	 * @param stock          변경하려고 하는 상품 개수 (선택)
	 * @author dereck-jun
	 */
	@Transactional
	public void patchProduct(
		Long currentUserId,
		Long storeId,
		Long productId,
		String productName,
		Long price,
		List<Long> categoryIdList,
		String description,
		Long stock
	) {
		Store getStore = getStoreOrElseThrow(storeId);

		if (!getStore.getSeller().getId().equals(currentUserId)) {
			throw new ForbiddenException(StoreErrorCode.NOT_STORE_OWNER);
		}

		Product getProduct = productRepository.findActiveProductByStoreIdAndProductId(storeId, productId)
			.orElseThrow(() -> new NotFoundException(ProductErrorCode.PRODUCT_NOT_FOUND));

		if (StringUtils.hasText(productName)) {
			if (getProduct.getName().equalsIgnoreCase(productName)) {
				throw new InvalidRequestException(ProductErrorCode.PRODUCT_NAME_SAME_AS_OLD);
			}

			if (productRepository.existsByStoreIdAndProductName(storeId, productName)) {
				throw new ConflictException(ProductErrorCode.DUPLICATE_PRODUCT_NAME);
			}

			getProduct.patchName(productName);
		}

		if (price != null) {
			getProduct.patchPrice(price);
		}

		if (StringUtils.hasText(description)) {
			getProduct.patchDescription(description);
		}

		if (stock != null) {
			getProduct.patchStock(stock);
		}

		// 상품에 연결된 모든 카테고리 연결 해제
		productCategoryRepository.deleteByProductId(productId);

		if (categoryIdList != null && !categoryIdList.isEmpty()) {
			linkCategoriesToProduct(categoryIdList, getProduct);
		}
	}

	/**
	 * 상품 삭제 (논리적 삭제)
	 *
	 * @param currentUserId 로그인한 사용자 ID (필수)
	 * @param storeId       가게 ID (필수)
	 * @param productId     삭제할 상품 ID (필수)
	 * @author dereck-jun
	 */
	@Transactional
	public void deleteProduct(Long currentUserId, Long storeId, Long productId) {
		Store getStore = getStoreOrElseThrow(storeId);

		if (!getStore.getSeller().getId().equals(currentUserId)) {
			throw new ForbiddenException(StoreErrorCode.NOT_STORE_OWNER);
		}

		Product getProduct = productRepository.findActiveProductByStoreIdAndProductId(storeId, productId)
			.orElseThrow(() -> new NotFoundException(ProductErrorCode.PRODUCT_NOT_FOUND));

		// 삭제하려는 상품과 연결된 카테고리가 있을 경우 연결 해제 (매핑 테이블에서 내용 삭제)
		if (productCategoryRepository.existsByProductId(productId)) {
			productCategoryRepository.deleteByProductId(productId);
		}

		getProduct.markDeleted();
	}

	@Transactional
	public void decreaseStock(Cart cart, Map<Long, Product> productMap) {
		for (CartItem cartItem : cart.getCart()) {
			Product product = productMap.get(cartItem.getProductId());
			product.decreaseStock(cartItem.getQuantity());
		}
	}

	private Store getStoreOrElseThrow(Long storeId) {
		return storeRepository.findActiveStoreById(storeId)
			.orElseThrow(() -> new NotFoundException(StoreErrorCode.STORE_NOT_FOUND));
	}

	private void linkCategoriesToProduct(List<Long> categoryIdList, Product product) {
		// 새로 등록할 카테고리 조회
		List<Category> categoryList = categoryRepository.findAllById(categoryIdList);

		// 조회된 내용과 요청한 내용의 크기가 다르면 요청 내용 중 카테고리가 없는 항목이 존재한다는 의미
		if (categoryIdList.size() != categoryList.size()) {
			throw new NotFoundException(CategoryErrorCode.CATEGORY_NOT_FOUND);
		}

		// 새로 연결한 카테고리 저장
		for (Category category : categoryList) {
			productCategoryRepository.save(
				ProductCategory.builder()
					.category(category)
					.product(product)
					.build()
			);
		}
	}


	private Map<Long, List<String>> getProductCategoryNameMap(List<Long> productIds) {
		List<Object[]> results = productCategoryRepository.findCategoryNamesByProductIds(productIds);
		Map<Long, List<String>> categoryNameMap = new HashMap<>();

		for (Object[] row : results) {
			Long productId = (Long) row[0];
			String categoryName = (String) row[1];
			categoryNameMap.computeIfAbsent(productId, k -> new ArrayList<>()).add(categoryName);
		}

		return categoryNameMap;
	}
}
