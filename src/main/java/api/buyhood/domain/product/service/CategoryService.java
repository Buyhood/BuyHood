package api.buyhood.domain.product.service;

import api.buyhood.domain.product.dto.response.CreateCategoryRes;
import api.buyhood.domain.product.dto.response.GetCategoryRes;
import api.buyhood.domain.product.dto.response.PageCategoryRes;
import api.buyhood.domain.product.entity.Category;
import api.buyhood.domain.product.repository.CategoryRepository;
import api.buyhood.global.common.exception.ConflictException;
import api.buyhood.global.common.exception.InvalidRequestException;
import api.buyhood.global.common.exception.NotFoundException;
import api.buyhood.global.common.exception.enums.CategoryErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryRepository categoryRepository;

	/**
	 * 카테고리 등록
	 *
	 * @param categoryName
	 * @param parentId
	 */
	@Transactional
	public CreateCategoryRes createCategory(Long parentId, String categoryName) {
		Category parent = null;

		if (parentId != null && parentId != 0) {
			parent = categoryRepository.findById(parentId)
				.orElseThrow(() -> new NotFoundException(CategoryErrorCode.CATEGORY_NOT_FOUND));

			if (categoryRepository.existsByParentIdAndName(parentId, categoryName)) {
				throw new ConflictException(CategoryErrorCode.DUPLICATE_CATEGORIES);
			}
		} else {
			if (categoryRepository.existsByParentIsNullAndName(categoryName)) {
				throw new ConflictException(CategoryErrorCode.DUPLICATE_CATEGORIES);
			}
		}

		int depth = parent == null ? 0 : parent.getDepth() + 1;

		if (depth >= 3) {
			throw new InvalidRequestException(CategoryErrorCode.MAX_DEPTH_OVER);
		}

		Category category = Category.builder()
			.depth(depth)
			.name(categoryName)
			.parent(parent)
			.build();

		return CreateCategoryRes.of(categoryRepository.save(category));
	}

	/**
	 * 카테고리 단건 조회
	 *
	 * @param categoryId
	 */
	@Transactional(readOnly = true)
	public GetCategoryRes getCategory(Long categoryId) {
		Category getCategory = categoryRepository.findById(categoryId)
			.orElseThrow(() -> new NotFoundException(CategoryErrorCode.CATEGORY_NOT_FOUND));
		return GetCategoryRes.of(getCategory);
	}

	/**
	 * 카테고리 전체 페이징 조회
	 *
	 * @param pageable
	 */
	@Transactional(readOnly = true)
	public Page<PageCategoryRes> getAllCategories(Pageable pageable) {
		PageRequest pageRequest =
			PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Direction.ASC, "depth", "name");
		return PageCategoryRes.of(categoryRepository.findAll(pageRequest));
	}

	/**
	 * 카테고리 depth 별 조회
	 *
	 * @param depth
	 * @param pageable
	 */
	@Transactional(readOnly = true)
	public Page<PageCategoryRes> getDepthCategories(int depth, Pageable pageable) {
		PageRequest pageRequest =
			PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Direction.ASC, "name");
		return PageCategoryRes.of(categoryRepository.getCategoriesByDepth(depth, pageRequest));
	}

	/**
	 * 카테고리 내용 수정
	 *
	 * @param categoryId
	 * @param newCategoryName
	 */
	@Transactional
	public void patchCategory(Long categoryId, String newCategoryName) {
		Category getCategory = categoryRepository.findById(categoryId)
			.orElseThrow(() -> new NotFoundException(CategoryErrorCode.CATEGORY_NOT_FOUND));

		if (getCategory.getName().equalsIgnoreCase(newCategoryName)) {
			throw new InvalidRequestException(CategoryErrorCode.CATEGORY_NAME_SAME_AS_OLD);
		}

		if (getCategory.getParent() != null) {
			if (categoryRepository.existsByParentIdAndName(getCategory.getParent().getId(), newCategoryName)) {
				throw new ConflictException(CategoryErrorCode.DUPLICATE_CATEGORIES);
			}
		} else {
			if (categoryRepository.existsByParentIsNullAndName(newCategoryName)) {
				throw new ConflictException(CategoryErrorCode.DUPLICATE_CATEGORIES);
			}
		}

		getCategory.patchCategory(newCategoryName);
	}

	/**
	 * 카테고리 삭제 (물리적 삭제 방식)
	 *
	 * @param categoryId
	 */
	@Transactional
	public void deleteCategory(Long categoryId) {
		Category getCategory = categoryRepository.findById(categoryId)
			.orElseThrow(() -> new NotFoundException(CategoryErrorCode.CATEGORY_NOT_FOUND));
		categoryRepository.delete(getCategory);
	}

}
