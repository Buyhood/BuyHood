package api.buyhood.domain.cart.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class CartItemRes {

	private final Long productId;
	private final int quantity;

	public static CartItemRes of(Long productId, int quantity) {
		return CartItemRes.builder()
			.productId(productId)
			.quantity(quantity)
			.build();
	}
}
