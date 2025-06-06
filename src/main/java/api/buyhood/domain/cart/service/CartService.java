package api.buyhood.domain.cart.service;

import api.buyhood.domain.cart.dto.request.CartReq;
import api.buyhood.domain.cart.dto.request.CreateCartReq;
import api.buyhood.domain.cart.dto.response.CartRes;
import api.buyhood.domain.cart.entity.Cart;
import api.buyhood.domain.cart.entity.CartItem;
import api.buyhood.domain.cart.repository.CartRepository;
import api.buyhood.domain.product.entity.Product;
import api.buyhood.domain.product.repository.ProductRepository;
import api.buyhood.domain.user.entity.User;
import api.buyhood.domain.user.repository.UserRepository;
import api.buyhood.exception.InvalidRequestException;
import api.buyhood.exception.NotFoundException;
import api.buyhood.security.AuthUser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static api.buyhood.errorcode.CartErrorCode.MULTIPLE_STORE_NOT_ALLOWED;
import static api.buyhood.errorcode.CartErrorCode.NOT_FOUND_CART;
import static api.buyhood.errorcode.ProductErrorCode.PRODUCT_NOT_FOUND;
import static api.buyhood.errorcode.UserErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CartService {

	private final CartRepository cartRepository;
	private final UserRepository userRepository;
	private final ProductRepository productRepository;

	@Transactional
	public CartRes addItemsToCart(AuthUser authUser, CreateCartReq createCartReq) {
		User user = userRepository.findByEmail(authUser.getEmail())
			.orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

		List<Long> productIdList = createCartReq.getCartItems().stream()
			.map(CartReq::getProductId)
			.distinct()
			.toList();

		// 조회된 상품의 ID 수가 요청된 상품의 ID 수보다 적다면 존재하지않는 상품이 있다는 의미(뭐가 없는진 모름)
		List<Product> products = productRepository.findAllById(productIdList);
		if (products.size() < productIdList.size()) {
			throw new NotFoundException(PRODUCT_NOT_FOUND);

		}

		// 가게 중복 검증
		validateSingleStoreInCart(products);

		List<CartItem> cartItemList = createCartReq.getCartItems().stream()
			.map(item ->
				CartItem.builder()
					.productId(item.getProductId())
					.quantity(item.getQuantity())
					.build()
			)
			.toList();

		Cart cart = Cart.builder()
			.cart(cartItemList)
			.build();
		cartRepository.add(user.getId(), cart);

		return CartRes.of(cart);
	}

	@Transactional(readOnly = true)
	public CartRes findCart(AuthUser authUser) {
		User user = userRepository.findByEmail(authUser.getEmail())
			.orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

		if (!cartRepository.existsCart(user.getId())) {
			throw new NotFoundException(NOT_FOUND_CART);
		}

		Cart cart = cartRepository.findCart(user.getId());

		return CartRes.of(cart);
	}

	@Transactional
	public void clearCart(AuthUser authUser) {
		User user = userRepository.findByEmail(authUser.getEmail())
			.orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

		if (!cartRepository.existsCart(user.getId())) {
			throw new NotFoundException(NOT_FOUND_CART);
		}

		cartRepository.clearCart(user.getId());
	}

	private void validateSingleStoreInCart(List<Product> products) {
		long storeCount = products.stream()
			.map(product -> product.getStore().getId())
			.distinct()
			.count();

		if (storeCount != 1) {
			throw new InvalidRequestException(MULTIPLE_STORE_NOT_ALLOWED);
		}
	}
}
