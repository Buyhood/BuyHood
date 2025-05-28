package api.buyhood.domain.product.service;

import api.buyhood.domain.cart.entity.Cart;
import api.buyhood.domain.cart.entity.CartItem;
import api.buyhood.domain.product.dto.response.RegisteringProductRes;
import api.buyhood.domain.product.entity.Category;
import api.buyhood.domain.product.entity.Product;
import api.buyhood.domain.product.repository.CategoryRepository;
import api.buyhood.domain.product.repository.ProductRepository;
import api.buyhood.global.common.exception.NotFoundException;
import api.buyhood.global.common.exception.enums.CategoryErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

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

	@Transactional
	public void decreaseStock(Cart cart, Map<Long, Product> productMap) {
		for (CartItem cartItem : cart.getCart()) {
			Product product = productMap.get(cartItem.getProductId());
			product.decreaseStock(cartItem.getQuantity());
		}
	}
}
