package api.buyhood.productcategory.dto.response;

import api.buyhood.productcategory.entity.ProductCategory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateProductCategoryRes {

	private final Long categoryId;
	private final int depth;
	private final String categoryName;
	private final Long parentId;

	public static CreateProductCategoryRes of(ProductCategory category) {
		return new CreateProductCategoryRes(
			category.getId(),
			category.getDepth(),
			category.getName(),
			category.getParent() == null
				? null : category.getParent().getId()
		);
	}

}
