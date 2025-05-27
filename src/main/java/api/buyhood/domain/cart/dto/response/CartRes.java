package api.buyhood.domain.cart.dto.response;

import api.buyhood.domain.cart.entity.Cart;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CartRes {
    private final Cart cart;

    @Builder
    private CartRes(Cart cart) {
        this.cart = cart;
    }
}
