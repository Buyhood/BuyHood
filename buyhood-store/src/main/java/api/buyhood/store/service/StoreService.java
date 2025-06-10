package api.buyhood.store.service;

import api.buyhood.dto.storecategory.StoreCategoryFeignDto;
import api.buyhood.dto.user.UserFeignDto;
import api.buyhood.errorcode.CategoryErrorCode;
import api.buyhood.errorcode.CommonErrorCode;
import api.buyhood.errorcode.StoreErrorCode;
import api.buyhood.exception.ConflictException;
import api.buyhood.exception.InvalidRequestException;
import api.buyhood.exception.NotFoundException;
import api.buyhood.store.client.StoreCategoryFeignClient;
import api.buyhood.store.client.UserFeignClient;
import api.buyhood.store.dto.response.GetStoreRes;
import api.buyhood.store.dto.response.PageStoreRes;
import api.buyhood.store.dto.response.RegisterStoreRes;
import api.buyhood.store.entity.Store;
import api.buyhood.store.repository.StoreRepository;
import feign.FeignException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreService {

	private final StoreRepository storeRepository;
	private final StoreCategoryFeignClient storeCategoryFeignClient;
	private final UserFeignClient userFeignClient;

	@Transactional
	public RegisterStoreRes registerStore(
		Long currentUserId,
		String storeName,
		String address,
		Long categoryId,
		Boolean isDeliverable,
		String description,
		String openedAt,
		String closedAt
	) {
		if (storeRepository.existsAllByName(storeName)) {
			throw new ConflictException(StoreErrorCode.DUPLICATE_STORE_NAME);
		}

		if (Boolean.FALSE.equals(storeCategoryFeignClient.existsById(categoryId))) {
			throw new NotFoundException(CategoryErrorCode.CATEGORY_NOT_FOUND);
		}

		Store newStore = Store.builder()
			.name(storeName)
			.address(address)
			.isDeliverable(isDeliverable)
			.description(description)
			.openedAt(parseToLocalTime(openedAt))
			.closedAt(parseToLocalTime(closedAt))
			.sellerId(currentUserId)
			.storeCategoryId(categoryId)
			.build();

		return RegisterStoreRes.of(storeRepository.save(newStore));
	}

	@Transactional
	public void patchStore(
		Long currentUserId,
		Long storeId,
		String storeName,
		String address,
		Boolean isDeliverable,
		String description,
		String openedAt,
		String closedAt
	) {
		Store getStore = getActiveStoreOrElseThrow(storeId);

		if (!getStore.getSellerId().equals(currentUserId)) {
			throw new InvalidRequestException(StoreErrorCode.NOT_STORE_OWNER);
		}

		if (StringUtils.hasText(storeName)) {
			if (getStore.getName().equalsIgnoreCase(storeName)) {
				throw new InvalidRequestException(StoreErrorCode.STORE_NAME_SAME_AS_OLD);
			}
			getStore.patchName(storeName);
		}

		if (StringUtils.hasText(address)) {
			if (getStore.getAddress().equalsIgnoreCase(address)) {
				throw new InvalidRequestException(StoreErrorCode.STORE_NAME_SAME_AS_OLD);
			}
			getStore.patchAddress(address);
		}

		if (isDeliverable != null) {
			getStore.patchIsDeliverable(isDeliverable);
		}

		if (StringUtils.hasText(description)) {
			getStore.patchDescription(description);
		}

		if (StringUtils.hasText(openedAt)) {
			getStore.patchOpenedAt(parseToLocalTime(openedAt));
		}

		if (StringUtils.hasText(closedAt)) {
			getStore.patchClosedAt(parseToLocalTime(closedAt));
		}
	}

	@Transactional
	public void inActiveStore(Long currentUserId, Long storeId) {
		Store getStore = getActiveStoreOrElseThrow(storeId);

		if (!getStore.getSellerId().equals(currentUserId)) {
			throw new InvalidRequestException(StoreErrorCode.NOT_STORE_OWNER);
		}

		getStore.markDeleted();
	}

	@Transactional(readOnly = true)
	public GetStoreRes getActiveStore(Long storeId) {
		Store getStore = getActiveStoreOrElseThrow(storeId);

		UserFeignDto getSellerRes = userFeignClient.getSellerResOrElseThrow(getStore.getSellerId());

		StoreCategoryFeignDto getStoreCategoryRes =
			storeCategoryFeignClient.getStoreCategoryResByIdOrElseThrow(getStore.getStoreCategoryId());

		return GetStoreRes.of(getStore, getSellerRes.getUsername(), getStoreCategoryRes.getCategoryName());
	}

	@Transactional(readOnly = true)
	public Page<PageStoreRes> getActiveStores(Pageable pageable) {
		PageRequest pageRequest =
			PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Direction.ASC, "name");

		Page<Store> getActiveStorePage = storeRepository.findActiveStores(pageRequest);

		List<Long> storeCategoryIds = getActiveStorePage.stream()
			.map(Store::getStoreCategoryId)
			.distinct()
			.toList();

		List<StoreCategoryFeignDto> getListStoreCategoryRes =
			storeCategoryFeignClient.getListStoreCategoryResByIds(storeCategoryIds);

		Map<Long, String> storeCategoryIdToName = getListStoreCategoryRes.stream()
			.collect(
				Collectors.toMap(StoreCategoryFeignDto::getCategoryId, StoreCategoryFeignDto::getCategoryName));

		return PageStoreRes.fromPageWithCategoryMap(getActiveStorePage, storeCategoryIdToName);
	}

	@Transactional(readOnly = true)
	public Page<PageStoreRes> getActiveStoresByCategoryName(String storeCategoryName, Pageable pageable) {
		PageRequest pageRequest =
			PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Direction.ASC, "name");

		Page<Store> getActiveStorePage = Page.empty();

		try {
			StoreCategoryFeignDto getStoreCategoryRes =
				storeCategoryFeignClient.getStoreCategoryResByNameOrElseThrow(storeCategoryName);

			getActiveStorePage =
				storeRepository.findActiveStoresByStoreCategoryId(getStoreCategoryRes.getCategoryId(), pageRequest);
		} catch (FeignException e) {
			if (e.status() == 404) {
				log.warn("[FeignException]: 카테고리 '{}' 를 찾을 수 없어 빈 페이지 반환", storeCategoryName);
			} else {
				log.error("[FeignException]: Feign 오류 발생: {}", e.contentUTF8(), e);
				throw e;
			}
		}

		return PageStoreRes.fromPageWithCategoryName(getActiveStorePage, storeCategoryName);
	}

	private Store getActiveStoreOrElseThrow(Long storeId) {
		return storeRepository.findActiveStoreById(storeId)
			.orElseThrow(() -> new NotFoundException(StoreErrorCode.STORE_NOT_FOUND));
	}

	private LocalTime parseToLocalTime(String input) {
		if (input == null) {
			return null;
		}
		try {
			return LocalTime.parse(input, DateTimeFormatter.ofPattern("HH:mm"));
		} catch (DateTimeParseException dtpe) {
			log.error("[DateTimeParseException]: 기대 포맷={}, 입력값={}\n", "HH:mm", dtpe.getParsedString(), dtpe);
			throw new InvalidRequestException(CommonErrorCode.INVALID_TIME_FORMAT);
		}
	}
}
