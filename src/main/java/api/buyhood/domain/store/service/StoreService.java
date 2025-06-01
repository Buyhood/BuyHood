package api.buyhood.domain.store.service;

import api.buyhood.domain.seller.entity.Seller;
import api.buyhood.domain.seller.repository.SellerRepository;
import api.buyhood.domain.store.dto.response.GetStoreRes;
import api.buyhood.domain.store.dto.response.PageStoreRes;
import api.buyhood.domain.store.dto.response.RegisterStoreRes;
import api.buyhood.domain.store.entity.Store;
import api.buyhood.domain.store.repository.StoreRepository;
import api.buyhood.global.common.exception.ConflictException;
import api.buyhood.global.common.exception.ForbiddenException;
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

	/**
	 * 가게 등록
	 *
	 * @param currentUserId 로그인한 사업자 ID (필수)
	 * @param storeName     가게 이름 (필수)
	 * @param address       가게 주소 (필수)
	 * @param isDeliverable 가게 자체 배달 가능 여부 (필수)
	 * @param description   가게 설명 (선택)
	 * @param openedAt      가게 여는 시간 (선택)
	 * @param closedAt      가게 닫는 시간 (선택)
	 * @author dereck-jun
	 */
	@Transactional
	public RegisterStoreRes registerStore(
		Long currentUserId,
		String storeName,
		String address,
		Boolean isDeliverable,
		String description,
		LocalTime openedAt,
		LocalTime closedAt
	) {
		Seller getSeller = sellerRepository.findActiveSellerById(currentUserId)
			.orElseThrow(() -> new NotFoundException(SellerErrorCode.SELLER_NOT_FOUND));

		if (storeRepository.existsByName(storeName)) {
			throw new ConflictException(StoreErrorCode.DUPLICATE_STORE_NAME);
		}

		Store store = Store.builder()
			.name(storeName)
			.address(address)
			.seller(getSeller)
			.isDeliverable(isDeliverable)
			.description(description)
			.openedAt(openedAt)
			.closedAt(closedAt)
			.build();

		storeRepository.save(store);

		return RegisterStoreRes.of(store, getSeller.getId());
	}

	/**
	 * 가게 단건 조회
	 *
	 * @param storeId 조회할 가게의 ID
	 * @author dereck-jun
	 */
	@Transactional(readOnly = true)
	public GetStoreRes getStore(Long storeId) {
		Store getStore = storeRepository.findActiveStoreByIdFetchSeller(storeId)
			.orElseThrow(() -> new NotFoundException(StoreErrorCode.STORE_NOT_FOUND));

		return GetStoreRes.of(getStore);
	}

	/**
	 * 가게 전체 조회
	 *
	 * @param pageable
	 * @author dereck-jun
	 */
	@Transactional(readOnly = true)
	public Page<PageStoreRes> getAllStore(Pageable pageable) {
		PageRequest pageRequest =
			PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Direction.ASC, "name");

		Page<Store> storePage = storeRepository.findActiveStores(pageRequest);
		return PageStoreRes.of(storePage);
	}

	/**
	 * 가게 키워드 조회
	 *
	 * @param keyword  가게 이름에 대한 키워드
	 * @param pageable
	 * @author dereck-jun
	 */
	@Transactional(readOnly = true)
	public Page<PageStoreRes> getStoreByKeyword(String keyword, Pageable pageable) {
		PageRequest pageRequest =
			PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Direction.ASC, "name");

		Page<Store> storePage = storeRepository.findActiveStoresByNameLike(keyword, pageRequest);
		return PageStoreRes.of(storePage);
	}

	/**
	 * 가게 수정
	 *
	 * @param storeId       수정할 가게 ID (필수)
	 * @param storeName     변경하려고 하는 가게 이름 (선택)
	 * @param address       변경하려고 하는 가게 주소 (선택)
	 * @param sellerId      변경하려고 하는 사업자 ID (선택)
	 * @param isDeliverable 변경하려고 하는 가게 배달 가능 여부 (선택)
	 * @param description   변경하려고 하는 가게 설명 (선택)
	 * @param openedAt      변경하려고 하는 가게 여는 시간 (선택)
	 * @param closedAt      변경하려고 하는 가게 닫는 시간 (선택)
	 * @author dereck-jun
	 */
	@Transactional
	public void patchStore(
		Long currentUserId,
		Long storeId,
		String storeName,
		String address,
		Long sellerId,
		Boolean isDeliverable,
		String description,
		LocalTime openedAt,
		LocalTime closedAt
	) {
		Store getStore = storeRepository.findActiveStoreByIdFetchSeller(storeId)
			.orElseThrow(() -> new NotFoundException(StoreErrorCode.STORE_NOT_FOUND));

		if (!currentUserId.equals(getStore.getSeller().getId())) {
			throw new ForbiddenException(StoreErrorCode.NOT_STORE_OWNER);
		}

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
			Seller getSeller = sellerRepository.findActiveSellerById(sellerId)
				.orElseThrow(() -> new NotFoundException(SellerErrorCode.SELLER_NOT_FOUND));
			getStore.patchSeller(getSeller);
		}

		if (isDeliverable != null) {
			getStore.patchIsDeliverable(isDeliverable);
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

	/**
	 * 가게 폐업 (논리적 삭제)
	 *
	 * @param storeId 폐업할 가게 ID
	 */
	@Transactional
	public void deleteStore(Long currentUserId, Long storeId) {
		Store getStore = storeRepository.findActiveStoreByIdFetchSeller(storeId)
			.orElseThrow(() -> new NotFoundException(StoreErrorCode.STORE_NOT_FOUND));

		if (!getStore.getSeller().getId().equals(currentUserId)) {
			throw new ForbiddenException(StoreErrorCode.NOT_STORE_OWNER);
		}

		getStore.markDeleted();
	}
}
