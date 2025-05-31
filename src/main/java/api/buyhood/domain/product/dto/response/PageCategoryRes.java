package api.buyhood.domain.product.dto.response;

import api.buyhood.domain.product.entity.Category;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PageCategoryRes {

	private final Long categoryId;
	private final int depth;
	private final String categoryName;

	public static Page<PageCategoryRes> of(Page<Category> categoryPage) {
		return categoryPage.map(category ->
			new PageCategoryRes(
				category.getId(),
				category.getDepth(),
				category.getName()
			)
		);
	}
}
