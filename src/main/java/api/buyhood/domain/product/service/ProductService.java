package api.buyhood.domain.product.service;

import api.buyhood.domain.cart.entity.Cart;
import api.buyhood.domain.cart.entity.CartItem;
import api.buyhood.domain.product.dto.response.GetProductRes;
import api.buyhood.domain.product.dto.response.PageProductRes;
import api.buyhood.domain.product.dto.response.RegisterProductRes;
import api.buyhood.domain.product.entity.Category;
import api.buyhood.domain.product.entity.CategoryProduct;
import api.buyhood.domain.product.entity.Product;
import api.buyhood.domain.product.repository.CategoryProductRepository;
import api.buyhood.domain.product.repository.CategoryRepository;
import api.buyhood.domain.product.repository.ProductRepository;
import api.buyhood.global.common.exception.ConflictException;
import api.buyhood.global.common.exception.InvalidRequestException;
import api.buyhood.global.common.exception.NotFoundException;
import api.buyhood.global.common.exception.enums.CategoryErrorCode;
import api.buyhood.global.common.exception.enums.ProductErrorCode;
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
	private final CategoryProductRepository categoryProductRepository;

	/**
	 * 상품 등록
	 *
	 * @param productName    상품 이름 (필수)
	 * @param price          상품 가격 (필수)
	 * @param stock          상품 개수 (필수)
	 * @param categoryIdList 상품 카테고리 (선택)
	 * @param description    상품 설명 (선택)
	 */
	@Transactional
	public RegisterProductRes registerProduct(
		String productName,
		Long price,
		Long stock,
		List<Long> categoryIdList,
		String description
	) {
		Product product = Product.builder()
			.name(productName)
			.price(price)
			.description(description)
			.stock(stock)
			.build();

		// 중간 테이블 매핑
		if (categoryIdList != null && !categoryIdList.isEmpty()) {
			linkCategoriesToProduct(categoryIdList, product);
		}

		productRepository.save(product);

		List<String> categoryNameList = categoryRepository.findCategoryNamesByCategoryIds(categoryIdList);

		return RegisterProductRes.of(product, categoryNameList);
	}

	/**
	 * 상품 단건 조회
	 *
	 * @param productId 상품 Id
	 */
	@Transactional(readOnly = true)
	public GetProductRes getProduct(Long productId) {
		// 상품 존재 여부 조회
		Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
			.orElseThrow(() -> new NotFoundException(ProductErrorCode.PRODUCT_NOT_FOUND));

		// 중간 엔티티에서 카테고리 id 목록 조회
		List<Long> categoryIds = categoryProductRepository.findCategoryIdsByProductId(productId);

		// 카테고리 이름 목록 조회
		List<String> categoryNames = categoryRepository.findCategoryNamesByCategoryIds(categoryIds);

		return GetProductRes.of(product, categoryNames);
	}

	/**
	 * 상품 전체 페이징 조회
	 *
	 * @param pageable
	 */
	@Transactional(readOnly = true)
	public Page<PageProductRes> getAllProducts(Pageable pageable) {
		PageRequest pageRequest =
			PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Direction.ASC, "name");

		Page<Product> productPage = productRepository.findAll(pageRequest);

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
	 * @param keyword  상품 이름에 대한 키워드 (선택)
	 * @param pageable
	 */
	@Transactional(readOnly = true)
	public Page<PageProductRes> getProductByKeyword(String keyword, Pageable pageable) {
		PageRequest pageRequest =
			PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Direction.ASC, "name");

		Page<Product> productPage = productRepository.findByKeyword(keyword, pageRequest);

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
	 * @param productId      변경할 상품의 Id (필수)
	 * @param productName    새로운 상품 이름 (선택)
	 * @param price          새로운 상품 가격 (선택)
	 * @param categoryIdList 연결 또는 연결 해제할 categoryIdList (선택)
	 * @param description    새로운 상품 설명 (선택)
	 * @param stock          새로운 상품 개수 (선택)
	 */
	@Transactional
	public void patchProduct(
		Long productId,
		String productName,
		Long price,
		List<Long> categoryIdList,
		String description,
		Long stock
	) {
		Product getProduct = productRepository.findById(productId)
			.orElseThrow(() -> new NotFoundException(ProductErrorCode.PRODUCT_NOT_FOUND));

		if (StringUtils.hasText(productName)) {
			if (getProduct.getName().equalsIgnoreCase(productName)) {
				throw new InvalidRequestException(ProductErrorCode.PRODUCT_NAME_SAME_AS_OLD);
			}

			if (productRepository.existsByName(productName)) {
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
		categoryProductRepository.deleteByProductId(productId);

		if (categoryIdList != null && !categoryIdList.isEmpty()) {
			linkCategoriesToProduct(categoryIdList, getProduct);
		}
	}

	/**
	 * 상품 삭제 (물리적 삭제)
	 *
	 * @param productId 삭제할 상품 Id (필수)
	 */
	@Transactional
	public void deleteProduct(Long productId) {
		Product getProduct = productRepository.findById(productId)
			.orElseThrow(() -> new NotFoundException(ProductErrorCode.PRODUCT_NOT_FOUND));

		categoryProductRepository.deleteByProductId(productId);

		productRepository.delete(getProduct);
	}

	@Transactional
	public void decreaseStock(Cart cart, Map<Long, Product> productMap) {
		for (CartItem cartItem : cart.getCart()) {
			Product product = productMap.get(cartItem.getProductId());
			product.decreaseStock(cartItem.getQuantity());
		}
	}

	private void linkCategoriesToProduct(List<Long> categoryIds, Product product) {
		// 새로 등록할 카테고리 조회
		List<Category> categoryList = categoryRepository.findAllById(categoryIds);

		// 조회된 내용과 요청한 내용의 크기가 다르면 요청 내용 중 카테고리가 없는 항목이 존재한다는 의미
		if (categoryIds.size() != categoryList.size()) {
			throw new NotFoundException(CategoryErrorCode.CATEGORY_NOT_FOUND);
		}

		// 새로 연결한 카테고리 저장
		for (Category category : categoryList) {
			categoryProductRepository.save(
				CategoryProduct.builder()
					.category(category)
					.product(product)
					.build()
			);
		}
	}


	private Map<Long, List<String>> getProductCategoryNameMap(List<Long> productIds) {
		List<Object[]> results = categoryProductRepository.findCategoryNamesByProductIds(productIds);
		Map<Long, List<String>> categoryNameMap = new HashMap<>();

		for (Object[] row : results) {
			Long productId = (Long) row[0];
			String categoryName = (String) row[1];
			categoryNameMap.computeIfAbsent(productId, k -> new ArrayList<>()).add(categoryName);
		}

		return categoryNameMap;
	}
}
