package api.buyhood.domain.store.service;

import api.buyhood.domain.seller.entity.Seller;
import api.buyhood.domain.seller.repository.SellerRepository;
import api.buyhood.domain.store.dto.response.GetStoreRes;
import api.buyhood.domain.store.dto.response.PageStoreRes;
import api.buyhood.domain.store.dto.response.RegisteringStoreRes;
import api.buyhood.domain.store.entity.Store;
import api.buyhood.domain.store.repository.StoreRepository;
import api.buyhood.global.common.exception.ConflictException;
import api.buyhood.global.common.exception.NotFoundException;
import api.buyhood.global.common.exception.enums.SellerErrorCode;
import api.buyhood.global.common.exception.enums.StoreErrorCode;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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

	@Transactional(readOnly = true)
	public GetStoreRes getStore(Long storeId) {
		Store getStore = storeRepository.findStoreById(storeId)
			.orElseThrow(() -> new NotFoundException(StoreErrorCode.STORE_NOT_FOUND));

		return GetStoreRes.of(getStore, getStore.getId());
	}

	@Transactional(readOnly = true)
	public Page<PageStoreRes> getAllStore(Pageable pageable) {
		PageRequest pageRequest =
			PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Direction.ASC, "name");

		Page<Store> storePage = storeRepository.findFetchAll(pageRequest);
		return PageStoreRes.of(storePage);
	}

	@Transactional(readOnly = true)
	public Page<PageStoreRes> getStoreByKeyword(String keyword, Pageable pageable) {
		PageRequest pageRequest =
			PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Direction.ASC, "name");

		Page<Store> storePage = storeRepository.findByKeyword(keyword, pageRequest);
		return PageStoreRes.of(storePage);
	}

	@Transactional
	public void patchStore(
		Long storeId,
		String storeName,
		String address,
		Long sellerId,
		String description,
		LocalTime openedAt,
		LocalTime closedAt
	) {
		Store getStore = storeRepository.findStoreById(storeId)
			.orElseThrow(() -> new NotFoundException(StoreErrorCode.STORE_NOT_FOUND));

		if (StringUtils.hasText(storeName)) {
			if (getStore.getName().equalsIgnoreCase(storeName)) {
				throw new ConflictException(StoreErrorCode.STORE_NAME_SAME_AS_OLD);
			}

			if (storeRepository.existsByName(storeName)) {
				throw new ConflictException(StoreErrorCode.DUPLICATE_STORE_NAME);
			}

			getStore.patchName(storeName);
		}

		if (StringUtils.hasText(address)) {
			getStore.patchAddress(address);
		}

		if (sellerId != null) {
			Seller getSeller = sellerRepository.findById(sellerId)
				.orElseThrow(() -> new NotFoundException(SellerErrorCode.SELLER_NOT_FOUND));
			getStore.patchSeller(getSeller);
		}

		if (StringUtils.hasText(description)) {
			getStore.patchDescription(description);
		}

		if (StringUtils.hasText(String.valueOf(openedAt))) {
			getStore.patchOpenedAt(openedAt);
		}

		if (StringUtils.hasText(String.valueOf(closedAt))) {
			getStore.patchClosedAt(closedAt);
		}
	}

	@Transactional
	public void deleteStore(Long storeId) {
		Store getStore = storeRepository.findById(storeId)
			.orElseThrow(() -> new NotFoundException(StoreErrorCode.STORE_NOT_FOUND));

		getStore.markDeleted();
	}
}
