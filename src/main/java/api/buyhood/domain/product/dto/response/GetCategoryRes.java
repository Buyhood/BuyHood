package api.buyhood.domain.product.dto.response;

import api.buyhood.domain.product.entity.Category;
import java.util.List;
import lombok.Getter;

@Getter
public class GetCategoryRes {

	private final int depth;
	private final String parentName;
	private final String categoryName;
	private final List<String> childrenNames;

	private GetCategoryRes(int depth, String parentName, String categoryName, List<String> childrenNames) {
		this.depth = depth;
		this.parentName = parentName;
		this.categoryName = categoryName;
		this.childrenNames = childrenNames;
	}

	public static GetCategoryRes of(Category category) {
		return new GetCategoryRes(
			category.getDepth(),
			category.getParent() == null
				? "root" : category.getParent().getName(),
			category.getName(),
			category.getChildren().stream().map(Category::getName).toList()
		);
	}
}
