package api.buyhood.category.dto.response.store;

import api.buyhood.category.entity.StoreCategory;
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
