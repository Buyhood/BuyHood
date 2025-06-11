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
public class RegisterStoreRes {

	private Long storeId;
	private String storeName;
	private String address;
	private Long sellerId;
	private Long categoryId;
	private boolean isDeliverable;
	private String description;
	private LocalTime openedAt;
	private LocalTime closedAt;

	public static RegisterStoreRes of(Store store) {
		return RegisterStoreRes.builder()
			.storeId(store.getId())
			.storeName(store.getName())
			.address(store.getAddress())
			.sellerId(store.getSellerId())
			.categoryId(store.getStoreCategoryId())
			.isDeliverable(store.getIsDeliverable())
			.description(store.getDescription())
			.openedAt(store.getOpenedAt())
			.closedAt(store.getClosedAt())
			.build();
	}
}
