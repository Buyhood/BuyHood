package api.buyhood.domain.store.dto.response;

import api.buyhood.domain.product.dto.response.GetProductRes;
import api.buyhood.domain.store.entity.Store;
import java.time.LocalTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GetStoreRes {

	private final Long storeId;
	private final String storeName;
	private final List<GetProductRes> productResList;
	private final String address;
	private final String sellerName;
	private final boolean isDeliverable;
	private final String description;
	private final LocalTime openedAt;
	private final LocalTime closedAt;

	public static GetStoreRes of(Store store, List<GetProductRes> productResList) {
		return new GetStoreRes(
			store.getId(),
			store.getName(),
			productResList,
			store.getAddress(),
			store.getSeller().getUsername(),
			store.isDeliverable(),
			store.getDescription(),
			store.getOpenedAt(),
			store.getClosedAt()
		);
	}

}
