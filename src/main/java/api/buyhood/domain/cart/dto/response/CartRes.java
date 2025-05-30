package api.buyhood.domain.cart.dto.response;

import api.buyhood.domain.cart.entity.Cart;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class CartRes {

	private final List<CartItemRes> cartItemRes;

	public static CartRes of(Cart cart) {

		List<CartItemRes> cartList = cart.getCart().stream()
			.map(cartItem ->
				CartItemRes.of(cartItem.getProductId(), cartItem.getQuantity())
			).toList();

		return CartRes.builder()
			.cartItemRes(cartList)
			.build();
	}
}
