package api.buyhood.domain.cart.dto.response;

import api.buyhood.domain.cart.entity.Cart;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class CartRes {
    private final List<CartItemRes> cartItemRes;

    @Builder
    private CartRes(List<CartItemRes> cartItemRes) {
        this.cartItemRes = cartItemRes;
    }

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
