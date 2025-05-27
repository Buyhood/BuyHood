package api.buyhood.domain.product.dto.response;

import api.buyhood.domain.product.entity.Category;
import lombok.Getter;

@Getter
public class CreateCategoryRes {

	private final String parentName;
	private final String categoryName;

	private CreateCategoryRes(String parentName, String categoryName) {
		this.parentName = parentName;
		this.categoryName = categoryName;
	}

	public static CreateCategoryRes of(Category category) {
		return new CreateCategoryRes(
			category.getParent() == null
				? "root" : category.getParent().getName(),
			category.getName()
		);
	}
}
