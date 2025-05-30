package api.buyhood.domain.store.dto.response;

import api.buyhood.domain.store.entity.Store;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PageStoreRes {

	private final Long storeId;
	private final String storeName;
	private final String address;
	private final LocalTime openedAt;
	private final LocalTime closedAt;

	public static Page<PageStoreRes> of(Page<Store> storePage) {
		return storePage.map(store ->
			new PageStoreRes(
				store.getId(),
				store.getName(),
				store.getAddress(),
				store.getOpenedAt(),
				store.getClosedAt()
			)
		);
	}

}
