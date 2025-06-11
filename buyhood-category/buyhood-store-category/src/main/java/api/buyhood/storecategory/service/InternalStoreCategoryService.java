package api.buyhood.storecategory.service;

import api.buyhood.dto.storecategory.StoreCategoryFeignDto;
import api.buyhood.errorcode.CategoryErrorCode;
import api.buyhood.exception.NotFoundException;
import api.buyhood.storecategory.entity.StoreCategory;
import api.buyhood.storecategory.repository.InternalStoreCategoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InternalStoreCategoryService {

	private final InternalStoreCategoryRepository internalStoreCategoryRepository;

	@Transactional(readOnly = true)
	public Boolean existsById(Long storeCategoryId) {
		return internalStoreCategoryRepository.existsById(storeCategoryId);
	}

	@Transactional(readOnly = true)
	public StoreCategoryFeignDto getStoreCategoryResByIdOrElseThrow(Long storeCategoryId) {
		StoreCategory getStoreCategory = internalStoreCategoryRepository.findById(storeCategoryId)
			.orElseThrow(() -> new NotFoundException(CategoryErrorCode.CATEGORY_NOT_FOUND));

		return new StoreCategoryFeignDto(getStoreCategory.getId(), getStoreCategory.getName());
	}

	@Transactional(readOnly = true)
	public StoreCategoryFeignDto getStoreCategoryResByNameOrElseThrow(String storeCategoryName) {
		StoreCategory getStoreCategory = internalStoreCategoryRepository.findByName(storeCategoryName)
			.orElseThrow(() -> new NotFoundException(CategoryErrorCode.CATEGORY_NOT_FOUND));

		return new StoreCategoryFeignDto(getStoreCategory.getId(), getStoreCategory.getName());
	}

	@Transactional(readOnly = true)
	public List<StoreCategoryFeignDto> getListStoreCategoryResByIds(List<Long> storeCategoryIds) {
		List<StoreCategory> getStoreCategories = internalStoreCategoryRepository.findByIds(storeCategoryIds);

		return getStoreCategories.stream()
			.map(storeCategory -> new StoreCategoryFeignDto(storeCategory.getId(), storeCategory.getName()))
			.toList();
	}


}
