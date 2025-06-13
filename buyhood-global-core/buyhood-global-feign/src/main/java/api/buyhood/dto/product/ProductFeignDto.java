package api.buyhood.dto.product;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProductFeignDto {

	private Long productId;
	private String productName;
	private Long price;
	private String description;
	private Long stock;
}
