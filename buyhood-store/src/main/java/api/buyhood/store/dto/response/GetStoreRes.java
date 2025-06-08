package api.buyhood.store.dto.response;

import api.buyhood.store.entity.Store;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GetStoreRes {

	private Long storeId;
	private String storeName;
	private String address;
	//TODO: 상품 리스트 추가 필요 (어떤 데이터로 가져올 것인지?)
	private String sellerName;
	private String storeCategoryName;
	private boolean isDeliverable;
	private String description;
	private LocalTime openedAt;
	private LocalTime closedAt;

	public static GetStoreRes of(Store store, String sellerName, String storeCategoryName) {
		return GetStoreRes.builder()
			.storeId(store.getId())
			.storeName(store.getName())
			.address(store.getAddress())
			.sellerName(sellerName)
			.storeCategoryName(storeCategoryName)
			.isDeliverable(store.getIsDeliverable())
			.description(store.getDescription())
			.openedAt(store.getOpenedAt())
			.closedAt(store.getClosedAt())
			.build();
	}
}
