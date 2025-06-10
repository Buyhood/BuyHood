package api.buyhood.store.dto.response;

import api.buyhood.store.entity.Store;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PageStoreRes {

	private Long storeId;
	private String storeName;
	private String storeCategoryName;
	private String address;


	public static Page<PageStoreRes> fromPageWithCategoryMap(Page<Store> storePage,
		Map<Long, String> storeCategoryIdToName) {
		return storePage.map(store ->
			PageStoreRes.builder()
				.storeId(store.getId())
				.storeName(store.getName())
				.storeCategoryName(storeCategoryIdToName.get(store.getStoreCategoryId()))
				.address(store.getAddress())
				.build()
		);
	}

	public static Page<PageStoreRes> fromPageWithCategoryName(Page<Store> storePage, String storeCategoryName) {
		return storePage.map(store ->
			PageStoreRes.builder()
				.storeId(store.getId())
				.storeName(store.getName())
				.storeCategoryName(storeCategoryName)
				.address(store.getAddress())
				.build()
		);
	}

}
