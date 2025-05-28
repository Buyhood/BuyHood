package api.buyhood.domain.product.service;

import api.buyhood.domain.product.dto.response.CreateCategoryRes;
import api.buyhood.domain.product.dto.response.GetCategoryRes;
import api.buyhood.domain.product.dto.response.PageCategoryRes;
import api.buyhood.domain.product.entity.Category;
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

	@Transactional
	public CreateCategoryRes createCategory(String categoryName, long parentId) {
		if (categoryRepository.existsByParentIdAndName(categoryName, parentId)) {
			throw new InvalidRequestException(CategoryErrorCode.DUPLICATE_CATEGORIES);
		}

		Category parent = categoryRepository.findById(parentId)
			.orElse(null);

		Category category = Category.builder()
			.name(categoryName)
			.parent(parent)
			.build();

		if (parent != null) {
			parent.addChildCategory(category);
		}

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

}
