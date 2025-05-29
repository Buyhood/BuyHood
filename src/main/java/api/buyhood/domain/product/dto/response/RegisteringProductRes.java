package api.buyhood.domain.product.dto.response;

import api.buyhood.domain.product.entity.Product;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class RegisteringProductRes {

	private final Long productId;
	private final String productName;
	private final Long price;
	private final Long stock;
	private final String categoryName;
	private final String description;

	public static RegisteringProductRes of(Product product, String categoryName) {
		return new RegisteringProductRes(
			product.getId(),
			product.getName(),
			product.getPrice(),
			product.getStock(),
			categoryName,
			product.getDescription()
		);
	}
}
