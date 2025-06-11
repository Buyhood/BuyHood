package api.buyhood.cart.entity;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Cart {

	private List<CartItem> cart = new ArrayList<>();

	@Builder
	public Cart(List<CartItem> cart) {
		this.cart = cart;
	}

}
