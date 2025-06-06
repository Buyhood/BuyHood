package api.buyhood.domain.product.repository;

import api.buyhood.domain.product.entity.ProductCategoryMapping;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ProductCategoryRepository extends JpaRepository<ProductCategoryMapping, Long> {

	@Query("select pc.categoryId from ProductCategoryMapping pc where pc.productId = :productId")
	List<Long> findCategoryIdsByProductId(Long productId);

	@Query("select pc.product.id, c.name from ProductCategory pc join pc.category c where pc.product.id in :productIds")
	List<Object[]> findCategoryNamesByProductIds(List<Long> productIds);

	@Modifying
	@Query("delete from Category pc where pc.category.id = :categoryId")
	void deleteByCategoryId(Long categoryId);

	@Query("select count(pc) > 0 from Category pc where pc.category.id = :categoryId")
	boolean existsByCategoryId(Long categoryId);

	@Modifying
	@Query("delete from Category pc where pc.product.id = :productId")
	void deleteByProductId(Long productId);

	@Query("select count(p) > 0 from Product p where p.id = :productId")
	boolean existsByProductId(Long productId);
}
