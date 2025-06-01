package api.buyhood.domain.product.repository;

import api.buyhood.domain.product.entity.CategoryProduct;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CategoryProductRepository extends JpaRepository<CategoryProduct, Long> {

	@Query("select cp.category.id from CategoryProduct cp where cp.product.id = :productId")
	List<Long> findCategoryIdsByProductId(Long productId);

	@Query("select cp.product.id, c.name from CategoryProduct cp join cp.category c where cp.product.id in :productIds")
	List<Object[]> findCategoryNamesByProductIds(List<Long> productIds);

	@Modifying
	@Query("delete from CategoryProduct cp where cp.category.id = :categoryId")
	void deleteByCategoryId(Long categoryId);

	@Query("select count(cp) > 0 from CategoryProduct cp where cp.category.id = :categoryId")
	boolean existsByCategoryId(Long categoryId);

	@Modifying
	@Query("delete from CategoryProduct cp where cp.product.id = :productId")
	void deleteByProductId(Long productId);

	@Query("select count(p) > 0 from Product p where p.id = :productId")
	boolean existsByProductId(Long productId);
}
