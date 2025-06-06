package api.buyhood.productcategory.dto.response.store;

import api.buyhood.productcategory.entity.StoreCategory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateStoreCategoryRes {

	private final Long categoryId;
	private final String categoryName;

	public static CreateStoreCategoryRes of(StoreCategory category) {
		return new CreateStoreCategoryRes(
			category.getId(),
			category.getName()
		);
	}

}
