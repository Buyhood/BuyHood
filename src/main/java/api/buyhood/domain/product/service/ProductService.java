package api.buyhood.domain.product.service;

import api.buyhood.domain.product.dto.response.GetProductRes;
import api.buyhood.domain.product.dto.response.PageProductRes;
import api.buyhood.domain.product.dto.response.RegisteringProductRes;
import api.buyhood.domain.product.entity.Category;
import api.buyhood.domain.product.entity.Product;
import api.buyhood.domain.product.repository.CategoryRepository;
import api.buyhood.domain.product.repository.ProductRepository;
import api.buyhood.global.common.exception.NotFoundException;
import api.buyhood.global.common.exception.enums.CategoryErrorCode;
import api.buyhood.global.common.exception.enums.ProductErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;

	@Transactional
	public RegisteringProductRes registerProduct(
		String productName,
		Long price,
		Long stock,
		Long categoryId,
		String description
	) {
		Category category = null;
		if (categoryId != null && categoryId != 0) {
			category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new NotFoundException(CategoryErrorCode.CATEGORY_NOT_FOUND));
		}

		Product product = Product.builder()
			.name(productName)
			.price(price)
			.category(category)
			.description(description)
			.stock(stock)
			.build();

		productRepository.save(product);

		return RegisteringProductRes.of(product, category != null ? category.getName() : null);
	}

	@Transactional(readOnly = true)
	public GetProductRes getProduct(Long productId) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new NotFoundException(ProductErrorCode.PRODUCT_NOT_FOUND));
		return GetProductRes.of(product, product.getCategory());
	}

	@Transactional(readOnly = true)
	public Page<PageProductRes> getAllProducts(Pageable pageable) {
		PageRequest pageRequest =
			PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Direction.ASC, "name");
		Page<Product> productPage = productRepository.findAll(pageRequest);
		return PageProductRes.of(productPage);
	}

	@Transactional(readOnly = true)
	public Page<PageProductRes> getProductByKeyword(String keyword, Pageable pageable) {
		PageRequest pageRequest =
			PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Direction.ASC, "name");
		Page<Product> productPage = productRepository.findByKeyword(keyword, pageRequest);
		return PageProductRes.of(productPage);
	}
}
