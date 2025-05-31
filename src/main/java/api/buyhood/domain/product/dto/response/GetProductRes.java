package api.buyhood.domain.product.dto.response;

import api.buyhood.domain.product.entity.Product;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GetProductRes {

	private final Long productId;
	private final String productName;
	private final Long price;
	private final List<String> categoryNameList;
	private final String description;
	private final Long stock;

	public static GetProductRes of(Product product, List<String> categoryNameList) {
		return new GetProductRes(
			product.getId(),
			product.getName(),
			product.getPrice(),
			categoryNameList,
			product.getDescription(),
			product.getStock()
		);
	}
}
