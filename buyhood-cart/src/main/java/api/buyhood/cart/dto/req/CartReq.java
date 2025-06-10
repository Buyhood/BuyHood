package api.buyhood.cart.dto.req;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CartReq {

	@NotNull(message = "상품 ID를 입력해주세요")
	private Long productId;

	@NotNull(message = "수량을 기입해주세요")
	private int quantity;
}
