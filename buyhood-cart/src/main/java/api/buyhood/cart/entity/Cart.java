package api.buyhood.cart.entity;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@NoArgsConstructor
public class Cart {

    private List<CartItem> cart = new ArrayList<>();

    @Builder
    public Cart(List<CartItem> cart) {
        this.cart = cart;
    }
}
