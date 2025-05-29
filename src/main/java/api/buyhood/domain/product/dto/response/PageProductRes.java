package api.buyhood.domain.product.dto.response;

import api.buyhood.domain.product.entity.Product;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PageProductRes {

	private final Long productId;
	private final String productName;
	private final Long price;
	private final String categoryName;
	private final Long stock;

	public static Page<PageProductRes> of(Page<Product> productPage) {
		return productPage.map(product ->
			new PageProductRes(
				product.getId(),
				product.getName(),
				product.getPrice(),
				product.getCategory().getName(),
				product.getStock()
			)
		);
	}
}
