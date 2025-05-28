package api.buyhood.domain.product.dto.response;

import api.buyhood.domain.product.entity.Category;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class PageCategoryRes {

	private final int depth;
	private final String categoryName;

	private PageCategoryRes(int depth, String categoryName) {
		this.depth = depth;
		this.categoryName = categoryName;
	}

	public static Page<PageCategoryRes> of(Page<Category> categoryPage) {
		return categoryPage
			.map(category -> new PageCategoryRes(category.getDepth(), category.getName()));
	}
}
