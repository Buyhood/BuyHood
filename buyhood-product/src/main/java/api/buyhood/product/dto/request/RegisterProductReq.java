package api.buyhood.product.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RegisterProductReq {

	@NotBlank(message = "상품 이름은 공백일 수 없습니다.")
	private final String productName;

	@NotNull(message = "상품 가격은 공백일 수 없습니다.")
	@Min(value = 0, message = "상품 가격은 0보다 작을 수 없습니다.")
	private final Long price;

	@NotNull(message = "상품 개수는 공백일 수 없습니다.")
	@Min(value = 0, message = "상품 개수는 0보다 작을 수 없습니다.")
	private final Long stock;

	private final List<Long> categoryIdList;
	private final String description;

}
