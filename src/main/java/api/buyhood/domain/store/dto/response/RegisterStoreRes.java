package api.buyhood.domain.store.dto.response;

import api.buyhood.domain.store.entity.Store;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class RegisterStoreRes {

	private final Long storeId;
	private final String storeName;
	private final String address;
	private final Long sellerId;
	private final boolean isDeliverable;
	private final String description;
	private final LocalTime openedAt;
	private final LocalTime closedAt;

	public static RegisterStoreRes of(Store store, Long sellerId) {
		return new RegisterStoreRes(
			store.getId(),
			store.getName(),
			store.getAddress(),
			sellerId,
			store.isDeliverable(),
			store.getDescription(),
			store.getOpenedAt(),
			store.getClosedAt()
		);
	}
}
