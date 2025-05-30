package api.buyhood.domain.store.service;

import api.buyhood.domain.seller.entity.Seller;
import api.buyhood.domain.seller.repository.SellerRepository;
import api.buyhood.domain.store.dto.response.RegisteringStoreRes;
import api.buyhood.domain.store.entity.Store;
import api.buyhood.domain.store.repository.StoreRepository;
import api.buyhood.global.common.exception.ConflictException;
import api.buyhood.global.common.exception.NotFoundException;
import api.buyhood.global.common.exception.enums.SellerErrorCode;
import api.buyhood.global.common.exception.enums.StoreErrorCode;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StoreService {

	private final StoreRepository storeRepository;
	private final SellerRepository sellerRepository;

	@Transactional
	public RegisteringStoreRes registerStore(
		String storeName,
		String address,
		Long sellerId,
		String description,
		LocalTime openedAt,
		LocalTime closedAt
	) {
		Seller getSeller = sellerRepository.findById(sellerId)
			.orElseThrow(() -> new NotFoundException(SellerErrorCode.SELLER_NOT_FOUND));

		if (storeRepository.existsByName(storeName)) {
			throw new ConflictException(StoreErrorCode.DUPLICATE_STORE_NAME);
		}

		Store store = Store.builder()
			.name(storeName)
			.address(address)
			.seller(getSeller)
			.description(description)
			.openedAt(openedAt)
			.closedAt(closedAt)
			.build();

		storeRepository.save(store);

		return RegisteringStoreRes.of(store, getSeller.getId());
	}
}
