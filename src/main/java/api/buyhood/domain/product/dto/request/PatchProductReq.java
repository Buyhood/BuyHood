package api.buyhood.domain.product.dto.request;

import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PatchProductReq {

	private final String productName;

	@Min(0)
	private final Long price;

	@Min(0)
	private final Long stock;
	private final List<Long> categoryIdList;
	private final String description;
}
