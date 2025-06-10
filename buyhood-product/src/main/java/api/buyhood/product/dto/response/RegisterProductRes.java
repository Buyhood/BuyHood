package api.buyhood.product.dto.response;

import api.buyhood.product.entity.Product;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class RegisterProductRes {

	private final Long productId;
	private final String productName;
	private final Long price;
	private final Long stock;
	private final List<String> categoryNameList;
	private final String description;

	public static RegisterProductRes of(Product product, List<String> categoryNameList) {
		return new RegisterProductRes(
			product.getId(),
			product.getName(),
			product.getPrice(),
			product.getStock(),
			categoryNameList,
			product.getDescription()
		);
	}
}
