package api.buyhood.domain.product.dto.response;

import api.buyhood.domain.product.entity.Category;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateCategoryRes {

	private final Long categoryId;
	private final String parentName;
	private final String categoryName;

	public static CreateCategoryRes of(Category category) {
		return new CreateCategoryRes(
			category.getId(),
			category.getParent() == null
				? "root" : category.getParent().getName(),
			category.getName()
		);
	}
}
