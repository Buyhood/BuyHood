package api.buyhood.domain.product.service;

import api.buyhood.domain.product.dto.response.CreateCategoryRes;
import api.buyhood.domain.product.entity.Category;
import api.buyhood.domain.product.repository.CategoryRepository;
import api.buyhood.global.common.exception.InvalidRequestException;
import api.buyhood.global.common.exception.enums.CategoryErrorCode;
import lombok.RequiredArgsConstructor;
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
}
