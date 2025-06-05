package api.buyhood.domain.product.dto.response;

import api.buyhood.domain.product.entity.Category;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GetCategoryRes {

	private final Long categoryId;
	private final int depth;
	private final String parentName;
	private final String categoryName;
	private final List<String> childrenNames;

	public static GetCategoryRes of(Category category) {
		return new GetCategoryRes(
			category.getId(),
			category.getDepth(),
			category.getParent() == null
				? "root" : category.getParent().getName(),
			category.getName(),
			category.getChildren().stream().map(Category::getName).toList()
		);
	}
}
