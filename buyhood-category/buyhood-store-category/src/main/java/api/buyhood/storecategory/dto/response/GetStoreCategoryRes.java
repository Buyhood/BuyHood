package api.buyhood.storecategory.dto.response;

import api.buyhood.storecategory.entity.StoreCategory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GetStoreCategoryRes {

	private final Long categoryId;
	private final String categoryName;

	public static GetStoreCategoryRes of(StoreCategory category) {
		return new GetStoreCategoryRes(
			category.getId(),
			category.getName()
		);
	}
}
