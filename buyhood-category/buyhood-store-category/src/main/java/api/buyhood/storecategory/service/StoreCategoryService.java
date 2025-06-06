package api.buyhood.storecategory.service;

import api.buyhood.errorcode.CategoryErrorCode;
import api.buyhood.exception.ConflictException;
import api.buyhood.exception.NotFoundException;
import api.buyhood.storecategory.dto.response.CreateStoreCategoryRes;
import api.buyhood.storecategory.dto.response.GetStoreCategoryRes;
import api.buyhood.storecategory.dto.response.PageStoreCategoryRes;
import api.buyhood.storecategory.entity.StoreCategory;
import api.buyhood.storecategory.repository.StoreCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StoreCategoryService {

	private final StoreCategoryRepository storeCategoryRepository;

	@Transactional
	public CreateStoreCategoryRes createStoreCategory(String name) {
		// 중복된 이름이 있을 경우
		if (storeCategoryRepository.existsByName(name)) {
			throw new ConflictException(CategoryErrorCode.DUPLICATE_CATEGORIES);
		}

		StoreCategory newStoreCategory = storeCategoryRepository.save(
			StoreCategory.builder()
				.name(name)
				.build()
		);

		return CreateStoreCategoryRes.of(newStoreCategory);
	}

	@Transactional
	public void patchStoreCategoryName(Long storeCategoryId, String name) {
		StoreCategory getStoreCategory = getStoreCategoryOrElseThrow(storeCategoryId);

		if (getStoreCategory.getName().equalsIgnoreCase(name)) {
			throw new ConflictException(CategoryErrorCode.CATEGORY_NAME_SAME_AS_OLD);
		}

		getStoreCategory.patchName(name);
	}

	@Transactional
	public void deleteStoreCategory(Long storeCategoryId) {
		if (!storeCategoryRepository.existsById(storeCategoryId)) {
			throw new NotFoundException(CategoryErrorCode.CATEGORY_NOT_FOUND);
		}

		storeCategoryRepository.deleteById(storeCategoryId);
	}

	@Transactional(readOnly = true)
	public GetStoreCategoryRes getStoreCategory(Long storeCategoryId) {
		StoreCategory getStoreCategory = getStoreCategoryOrElseThrow(storeCategoryId);
		return GetStoreCategoryRes.of(getStoreCategory);
	}

	@Transactional(readOnly = true)
	public Page<PageStoreCategoryRes> getAllStoreCategories(Pageable pageable) {
		PageRequest pageRequest =
			PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Direction.ASC, "name");

		Page<StoreCategory> storeCategoryPage = storeCategoryRepository.findAll(pageRequest);

		return PageStoreCategoryRes.of(storeCategoryPage);
	}

	@Transactional(readOnly = true)
	public Page<PageStoreCategoryRes> getStoreCategoriesByKeyword(String keyword, Pageable pageable) {
		PageRequest pageRequest =
			PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Direction.ASC, "name");

		Page<StoreCategory> storeCategoryPage = storeCategoryRepository.findByKeyword(keyword, pageRequest);

		return PageStoreCategoryRes.of(storeCategoryPage);
	}

	private StoreCategory getStoreCategoryOrElseThrow(Long storeCategoryId) {
		return storeCategoryRepository.findById(storeCategoryId)
			.orElseThrow(() -> new NotFoundException(CategoryErrorCode.CATEGORY_NOT_FOUND));
	}
}
