package api.buyhood.domain.cart.dto.response;

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

    public static CartRes of(List<CartItemRes> cartItemRes) {
        return CartRes.builder()
                .cartItemRes(cartItemRes)
                .build();
    }
}
