package api.buyhood.domain.product.repository;

import api.buyhood.domain.product.entity.Category;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Category, Long> {

	@Query("select count(c) > 0 from Category c where c.parent.id = :parentId and c.name = :categoryName")
	boolean existsByParentIdAndName(@Param("parentId") Long parentId, @Param("categoryName") String categoryName);

	@Query("select count(c) > 0 from Category c where c.parent.id is null and c.name = :categoryName")
	boolean existsByParentIsNullAndName(String categoryName);

	@Query(
		value = "select c from Category c where c.depth = :depth",
		countQuery = "select count(c) from Category c where c.depth = :depth")
	Page<Category> getCategoriesByDepth(@Param("depth") int depth, Pageable pageable);

	@Query("select c.name from Category c where c.id in :categoryIds")
	List<String> findCategoryNamesByCategoryIds(Collection<Long> categoryIds);
}
