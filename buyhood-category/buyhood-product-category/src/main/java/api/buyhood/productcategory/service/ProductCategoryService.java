package api.buyhood.productcategory.service;

import api.buyhood.errorcode.CategoryErrorCode;
import api.buyhood.exception.ConflictException;
import api.buyhood.exception.InvalidRequestException;
import api.buyhood.exception.NotFoundException;
import api.buyhood.productcategory.dto.response.CreateProductCategoryRes;
import api.buyhood.productcategory.dto.response.GetProductCategoryRes;
import api.buyhood.productcategory.dto.response.PageProductCategoryRes;
import api.buyhood.productcategory.entity.ProductCategory;
import api.buyhood.productcategory.repository.ProductCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductCategoryService {

	private final ProductCategoryRepository productCategoryRepository;

	@Transactional
	public CreateProductCategoryRes createProductCategory(String name, Long parentId) {
		ProductCategory parentCategory = null;

		if (parentId != null && parentId != 0) {
			parentCategory = getProductCategoryOrElseThrow(parentId);

			if (productCategoryRepository.existsByParentIdAndName(parentId, name)) {
				throw new ConflictException(CategoryErrorCode.DUPLICATE_CATEGORIES);
			}
		} else {
			if (productCategoryRepository.existsByParentIsNullAndName(name)) {
				throw new ConflictException(CategoryErrorCode.DUPLICATE_CATEGORIES);
			}
		}

		int depth = parentCategory == null ? 0 : parentCategory.getDepth() + 1;

		if (depth >= 3) {
			throw new InvalidRequestException(CategoryErrorCode.MAX_DEPTH_OVER);
		}

		ProductCategory newProductCategory = productCategoryRepository.save(
			ProductCategory.builder()
				.name(name)
				.parent(parentCategory)
				.depth(depth)
				.build()
		);

		return CreateProductCategoryRes.of(newProductCategory);
	}

	@Transactional
	public void patchProductCategoryName(Long productCategoryId, String name) {
		ProductCategory getProductCategory = getProductCategoryOrElseThrow(productCategoryId);

		if (getProductCategory.getName().equalsIgnoreCase(name)) {
			throw new InvalidRequestException(CategoryErrorCode.CATEGORY_NAME_SAME_AS_OLD);
		}

		if (getProductCategory.getParent() != null) {
			if (productCategoryRepository.existsByParentIdAndName(getProductCategory.getParent().getId(), name)) {
				throw new ConflictException(CategoryErrorCode.DUPLICATE_CATEGORIES);
			}
		} else {
			if (productCategoryRepository.existsByParentIsNullAndName(name)) {
				throw new ConflictException(CategoryErrorCode.DUPLICATE_CATEGORIES);
			}
		}

		getProductCategory.patchName(name);
	}

	@Transactional
	public void deleteProductCategory(Long productCategoryId) {
		if (!productCategoryRepository.existsById(productCategoryId)) {
			throw new NotFoundException(CategoryErrorCode.CATEGORY_NOT_FOUND);
		}

		productCategoryRepository.deleteById(productCategoryId);
	}

	@Transactional(readOnly = true)
	public GetProductCategoryRes getProductCategory(Long productCategoryId) {
		ProductCategory getProductCategory = getProductCategoryOrElseThrow(productCategoryId);
		return GetProductCategoryRes.of(getProductCategory);
	}

	@Transactional(readOnly = true)
	public Page<PageProductCategoryRes> getAllProductCategories(Pageable pageable) {
		PageRequest pageRequest =
			PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Direction.ASC, "depth", "name");

		Page<ProductCategory> productCategoryPage = productCategoryRepository.findAll(pageRequest);

		return PageProductCategoryRes.of(productCategoryPage);
	}

	@Transactional(readOnly = true)
	public Page<PageProductCategoryRes> getProductCategoriesByKeyword(String keyword, Pageable pageable) {
		PageRequest pageRequest =
			PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Direction.ASC, "depth", "name");

		Page<ProductCategory> productCategoryPage = productCategoryRepository.findByKeyword(keyword, pageRequest);

		return PageProductCategoryRes.of(productCategoryPage);
	}

	private ProductCategory getProductCategoryOrElseThrow(Long productCategoryId) {
		return productCategoryRepository.findById(productCategoryId)
			.orElseThrow(() -> new NotFoundException(CategoryErrorCode.CATEGORY_NOT_FOUND));
	}

}
