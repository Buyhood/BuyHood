package api.buyhood.domain.product.dto.response;

import api.buyhood.domain.product.entity.Product;
import java.util.List;
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
	private final List<String> categoryNameList;
	private final String description;

	public static RegisteringProductRes of(Product product, List<String> categoryNameList) {
		return new RegisteringProductRes(
			product.getId(),
			product.getName(),
			product.getPrice(),
			product.getStock(),
			categoryNameList,
			product.getDescription()
		);
	}
}
