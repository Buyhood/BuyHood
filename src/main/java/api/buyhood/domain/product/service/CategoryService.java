package api.buyhood.domain.product.service;

import api.buyhood.domain.product.dto.response.CreateCategoryRes;
import api.buyhood.domain.product.dto.response.GetCategoryRes;
import api.buyhood.domain.product.dto.response.PageCategoryRes;
import api.buyhood.domain.product.entity.Category;
import api.buyhood.domain.product.repository.CategoryProductRepository;
import api.buyhood.domain.product.repository.CategoryRepository;
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
	private final CategoryProductRepository categoryProductRepository;

	@Transactional
	public CreateCategoryRes createCategory(String categoryName, Long parentId) {
		Category parent = null;
		if (parentId != null && parentId != 0) {
			parent = categoryRepository.findById(parentId)
				.orElseThrow(() -> new NotFoundException(CategoryErrorCode.CATEGORY_NOT_FOUND));
		}

		if (categoryRepository.existsByParentIdAndName(categoryName, parentId)) {
			throw new InvalidRequestException(CategoryErrorCode.DUPLICATE_CATEGORIES);
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

	@Transactional(readOnly = true)
	public GetCategoryRes getCategory(Long categoryId) {
		Category getCategory = categoryRepository.findById(categoryId)
			.orElseThrow(() -> new NotFoundException(CategoryErrorCode.CATEGORY_NOT_FOUND));
		return GetCategoryRes.of(getCategory);
	}

	@Transactional(readOnly = true)
	public Page<PageCategoryRes> getAllCategories(Pageable pageable) {
		PageRequest pageRequest =
			PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Direction.ASC, "depth", "name");
		return PageCategoryRes.of(categoryRepository.findAll(pageRequest));
	}

	@Transactional(readOnly = true)
	public Page<PageCategoryRes> getDepthCategories(int depth, Pageable pageable) {
		PageRequest pageRequest =
			PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Direction.ASC, "name");
		return PageCategoryRes.of(categoryRepository.getCategoriesByDepth(depth, pageRequest));
	}

	@Transactional
	public void patchCategory(Long categoryId, String newCategoryName) {
		Category getCategory = categoryRepository.findById(categoryId)
			.orElseThrow(() -> new NotFoundException(CategoryErrorCode.CATEGORY_NOT_FOUND));
		getCategory.patchCategory(newCategoryName);
	}

	@Transactional
	public void deleteCategory(Long categoryId) {
		Category getCategory = categoryRepository.findById(categoryId)
			.orElseThrow(() -> new NotFoundException(CategoryErrorCode.CATEGORY_NOT_FOUND));

		// 삭제하려는 카테고리와 연결된 상품이 있을 경우 연결 해제 (매핑 테이블에서 내용 삭제)
		if (categoryProductRepository.existsByCategoryId(categoryId)) {
			categoryProductRepository.deleteByCategoryId(categoryId);
		}

		categoryRepository.delete(getCategory);
	}

}
