package api.buyhood.productcategory.service;

import api.buyhood.dto.productcategory.ProductCategoryFeignDto;
import api.buyhood.exception.InvalidRequestException;
import api.buyhood.exception.NotFoundException;
import api.buyhood.productcategory.entity.ProductCategory;
import api.buyhood.productcategory.repository.InternalProductCategoryRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static api.buyhood.errorcode.CategoryErrorCode.CATEGORY_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class InternalProductCategoryService {

	private final InternalProductCategoryRepository internalProductCategoryRepository;

	@Transactional(readOnly = true)
	public ProductCategoryFeignDto getCategoryOrElseThrow(Long categoryProductId) {
		ProductCategory getProductCategory = internalProductCategoryRepository.findById(categoryProductId)
			.orElseThrow(() -> new NotFoundException(CATEGORY_NOT_FOUND));

		Long parentId = null;
		List<Long> childrenIds = new ArrayList<>();

		if (getProductCategory.getParent() != null) {
			parentId = getProductCategory.getParent().getId();
		}

		if (!getProductCategory.getChildren().isEmpty()) {
			for (ProductCategory child : getProductCategory.getChildren()) {
				childrenIds.add(child.getId());
			}
		}

		return new ProductCategoryFeignDto(
			getProductCategory.getId(),
			getProductCategory.getName(),
			getProductCategory.getDepth(),
			parentId,
			childrenIds
		);
	}

	@Transactional(readOnly = true)
	public Boolean existsById(Long categoryProductId) {
		return internalProductCategoryRepository.existsById(categoryProductId);
	}

	@Transactional(readOnly = true)
	public Boolean existsByIds(List<Long> categoryProductIds) {
		for (Long categoryProductId : categoryProductIds) {
			if (!internalProductCategoryRepository.existsById(categoryProductId)) {
				throw new InvalidRequestException(CATEGORY_NOT_FOUND);
			}
		}
		return true;
	}
}
