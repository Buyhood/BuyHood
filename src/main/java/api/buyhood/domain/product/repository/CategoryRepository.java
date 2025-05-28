package api.buyhood.domain.product.repository;

import api.buyhood.domain.product.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Category, Long> {

	@Query(
		"select count(*) > 0 "
			+ "from Category c "
			+ "where c.name = :categoryName "
			+ "and ((:parentId = 0 and c.parent is null) "
			+ "or (c.parent.id = :parentId))")
	boolean existsByParentIdAndName(@Param("categoryName") String categoryName, @Param("parentId") Long parentId);

	@Query("select c from Category c where c.depth = :depth")
	Page<Category> getCategoriesByDepth(@Param("depth") int depth, Pageable pageable);

}
