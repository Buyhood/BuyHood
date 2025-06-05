package api.buyhood.domain.cart.dto.request;

import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCartReq {

	@Valid
	private List<CartReq> cartItems;
}
