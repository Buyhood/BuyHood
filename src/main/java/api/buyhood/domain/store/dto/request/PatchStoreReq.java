package api.buyhood.domain.store.dto.request;

import java.time.LocalTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PatchStoreReq {

	private final String storeName;
	private final String address;
	private final Long sellerId;
	private final Boolean isDeliverable;
	private final String description;
	private final LocalTime openedAt;
	private final LocalTime closedAt;

}
