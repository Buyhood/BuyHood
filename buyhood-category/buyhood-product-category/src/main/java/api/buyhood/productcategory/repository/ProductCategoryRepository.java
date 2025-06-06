package api.buyhood.productcategory.repository;

import api.buyhood.productcategory.entity.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

	@Query("select count(pc) > 0 from ProductCategory pc where pc.id = :parentId and lower(pc.name) = lower(:name)")
	boolean existsByParentIdAndName(@Param("parentId") Long parentId, @Param("name") String name);

	@Query("select count(pc) > 0 from ProductCategory pc where pc.id is null and lower(pc.name) = lower(:name)")
	boolean existsByParentIsNullAndName(String name);

	@Query(
		value = "select pc from ProductCategory pc where lower(pc.name) like concat('%', lower(:keyword), '%')",
		countQuery = "select count(pc) from ProductCategory pc where lower(pc.name) like concat('%', lower(:keyword), '%')"
	)
	Page<ProductCategory> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
