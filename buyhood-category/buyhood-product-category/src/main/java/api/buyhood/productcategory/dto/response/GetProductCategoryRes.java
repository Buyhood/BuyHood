package api.buyhood.productcategory.dto.response;

import api.buyhood.productcategory.entity.ProductCategory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GetProductCategoryRes {

	private final Long categoryId;
	private final int depth;
	private final String categoryName;
	private final Long parentId;

	public static GetProductCategoryRes of(ProductCategory category) {
		return new GetProductCategoryRes(
			category.getId(),
			category.getDepth(),
			category.getName(),
			category.getParent() == null
				? null : category.getParent().getId()
		);
	}
}
