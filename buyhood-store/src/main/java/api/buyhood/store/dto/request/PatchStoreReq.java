package api.buyhood.store.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PatchStoreReq {

	private final String storeName;
	private final String address;
	private final Boolean isDeliverable;
	private final String description;
	private final String openedAt;
	private final String closedAt;

}
