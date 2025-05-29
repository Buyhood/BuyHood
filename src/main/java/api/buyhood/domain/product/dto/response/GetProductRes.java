package api.buyhood.domain.product.dto.response;

import api.buyhood.domain.product.entity.Category;
import api.buyhood.domain.product.entity.Product;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GetProductRes {

	private final Long productId;
	private final String productName;
	private final Long price;
	private final String categoryName;
	private final String description;
	private final Long stock;

	public static GetProductRes of(Product product, Category category) {
		return new GetProductRes(
			product.getId(),
			product.getName(),
			product.getPrice(),
			category.getName(),
			product.getDescription(),
			product.getStock()
		);
	}
}
