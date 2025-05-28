package api.buyhood.domain.cart.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CartItemRes {
    private Long productId;
    private int quantity;

    @Builder
    public CartItemRes(Long productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public static CartItemRes of (Long productId, int quantity) {
        return CartItemRes.builder()
                .productId(productId)
                .quantity(quantity)
                .build();
    }
}
