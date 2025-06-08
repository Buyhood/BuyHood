package api.buyhood.productcategory.dto.response;

import api.buyhood.productcategory.entity.ProductCategory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PageProductCategoryRes {

	private final Long categoryId;
	private final int depth;
	private final String categoryName;
	private final Long parentId;

	public static Page<PageProductCategoryRes> of(Page<ProductCategory> categoryPage) {
		return categoryPage.map(category ->
			new PageProductCategoryRes(
				category.getId(),
				category.getDepth(),
				category.getName(),
				category.getParent() == null
					? null : category.getParent().getId()
			)
		);
	}
}
