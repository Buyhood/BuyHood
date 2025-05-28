package api.buyhood.domain.cart.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class Cart {
    private List<CartItem> cart = new ArrayList<>();

    @Builder
    public Cart(List<CartItem> cart) {
        this.cart = cart;
    }

    public static Cart of (List<CartItem> cartItemList) {
        return Cart.builder()
                .cart(cartItemList)
                .build();
    }
}
