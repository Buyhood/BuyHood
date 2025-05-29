package api.buyhood.domain.product.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PatchProductReq {

	private final String productName;
	private final Long price;
	private final Long stock;
	private final Long categoryId;
	private final String description;
}
