package api.buyhood.cart.dto.response;

import api.buyhood.cart.entity.Cart;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

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
