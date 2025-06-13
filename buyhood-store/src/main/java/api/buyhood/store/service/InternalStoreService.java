package api.buyhood.store.service;

import api.buyhood.dto.store.StoreFeignDto;
import api.buyhood.errorcode.SellerErrorCode;
import api.buyhood.exception.NotFoundException;
import api.buyhood.store.entity.Store;
import api.buyhood.store.repository.InternalStoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InternalStoreService {

	private final InternalStoreRepository internalStoreRepository;

	@Transactional(readOnly = true)
	public StoreFeignDto getStoreOrElseThrow(Long storeId) {
		Store getStore = internalStoreRepository.findActiveStoreById(storeId)
			.orElseThrow(() -> new NotFoundException(SellerErrorCode.SELLER_NOT_FOUND));

		return new StoreFeignDto(getStore.getId(), getStore.getName(), getStore.getSellerId());
	}

	@Transactional(readOnly = true)
	public Boolean existsById(Long storeId) {
		return internalStoreRepository.existsActiveStoreById(storeId);
	}
}
