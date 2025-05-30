package api.buyhood.domain.cart.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CartReq {

	@NotNull
	private Long productId;

	@NotNull
	private int quantity;
}
