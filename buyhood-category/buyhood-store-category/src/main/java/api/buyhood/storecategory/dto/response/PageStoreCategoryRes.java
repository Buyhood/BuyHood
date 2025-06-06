package api.buyhood.storecategory.dto.response;

import api.buyhood.storecategory.entity.StoreCategory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PageStoreCategoryRes {

	private final Long categoryId;
	private final String categoryName;

	public static Page<PageStoreCategoryRes> of(Page<StoreCategory> categoryPage) {
		return categoryPage.map(category ->
			new PageStoreCategoryRes(
				category.getId(),
				category.getName()
			)
		);
	}
}
