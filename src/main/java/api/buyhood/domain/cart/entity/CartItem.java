package api.buyhood.domain.cart.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CartItem {
    private Long productId;
    private int quantity;

    @Builder
    private CartItem(Long productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}
