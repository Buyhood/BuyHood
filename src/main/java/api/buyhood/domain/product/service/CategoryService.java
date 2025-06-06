package api.buyhood.domain.product.service;

import api.buyhood.domain.product.dto.response.CreateCategoryRes;
import api.buyhood.domain.product.dto.response.GetCategoryRes;
import api.buyhood.domain.product.dto.response.PageCategoryRes;
import api.buyhood.domain.product.entity.Category;
import api.buyhood.domain.product.repository.CategoryRepository;
import api.buyhood.domain.product.repository.ProductCategoryRepository;
import api.errorcode.CategoryErrorCode;
import api.exception.ConflictException;
import api.exception.InvalidRequestException;
import api.exception.NotFoundException;
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
	private final ProductCategoryRepository productCategoryRepository;

	/**
	 * 카테고리 생성
	 *
	 * @param categoryName 카테고리 이름 (필수)
	 * @param parentId     부모 카테고리 ID 값 (선택)
	 * @author dereck-jun
	 */
	@Transactional
	public CreateCategoryRes createCategory(String categoryName, Long parentId) {
		Category parent = null;
		if (parentId != null && parentId != 0) {
			parent = categoryRepository.findById(parentId)
				.orElseThrow(() -> new NotFoundException(CategoryErrorCode.CATEGORY_NOT_FOUND));
			// 하위 카테고리 중복 체크
			if (categoryRepository.existsByParentIdAndName(parentId, categoryName)) {
				throw new InvalidRequestException(CategoryErrorCode.DUPLICATE_CATEGORIES);
			}
		} else {
			// 최상위 카테고리 중복 체크
			if (categoryRepository.existsByParentIsNullAndName(categoryName)) {
				throw new InvalidRequestException(CategoryErrorCode.DUPLICATE_CATEGORIES);
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
	 * @param categoryId 조회할 카테고리 ID
	 * @author dereck-jun
	 */
	@Transactional(readOnly = true)
	public GetCategoryRes getCategory(Long categoryId) {
		Category getCategory = categoryRepository.findById(categoryId)
			.orElseThrow(() -> new NotFoundException(CategoryErrorCode.CATEGORY_NOT_FOUND));
		return GetCategoryRes.of(getCategory);
	}

	/**
	 * 카테고리 전체 조회
	 *
	 * @param pageable
	 * @author dereck-jun
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
	 * @param depth    조회할 카테고리의 depth
	 * @param pageable
	 * @author dereck-jun
	 */
	@Transactional(readOnly = true)
	public Page<PageCategoryRes> getDepthCategories(int depth, Pageable pageable) {
		PageRequest pageRequest =
			PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Direction.ASC, "name");
		return PageCategoryRes.of(categoryRepository.getCategoriesByDepth(depth, pageRequest));
	}

	/**
	 * 카테고리 수정
	 *
	 * @param categoryId      수정할 카테고리 ID
	 * @param newCategoryName 수정할 카테고리 이름
	 * @author dereck-jun
	 */
	@Transactional
	public void patchCategory(Long categoryId, String newCategoryName) {
		Category getCategory = categoryRepository.findById(categoryId)
			.orElseThrow(() -> new NotFoundException(CategoryErrorCode.CATEGORY_NOT_FOUND));

		// 부모가 있으면, 해당 부모 아래에서 중복 체크
		if (getCategory.getParent() != null) {
			Long parentId = getCategory.getParent().getId();
			if (categoryRepository.existsByParentIdAndName(parentId, newCategoryName)) {
				throw new ConflictException(CategoryErrorCode.DUPLICATE_CATEGORIES);
			}
		} else {
			// 부모가 없으면, 최상위 카테고리끼리 중복 체크
			if (categoryRepository.existsByParentIsNullAndName(newCategoryName)) {
				throw new ConflictException(CategoryErrorCode.DUPLICATE_CATEGORIES);
			}
		}

		getCategory.patchCategory(newCategoryName);
	}

	/**
	 * 카테고리 삭제 (물리적 삭제)
	 *
	 * @param categoryId 삭제할 카테고리 ID
	 * @author dereck-jun
	 */
	@Transactional
	public void deleteCategory(Long categoryId) {
		Category getCategory = categoryRepository.findById(categoryId)
			.orElseThrow(() -> new NotFoundException(CategoryErrorCode.CATEGORY_NOT_FOUND));

		// 삭제하려는 카테고리와 연결된 상품이 있을 경우 연결 해제 (매핑 테이블에서 내용 삭제)
		if (productCategoryRepository.existsByCategoryId(categoryId)) {
			productCategoryRepository.deleteByCategoryId(categoryId);
		}

		categoryRepository.delete(getCategory);
	}

}
