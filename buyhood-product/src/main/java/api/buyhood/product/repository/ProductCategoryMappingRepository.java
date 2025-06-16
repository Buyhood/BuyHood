package api.buyhood.product.repository;

import api.buyhood.product.entity.ProductCategoryMapping;
import feign.Param;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCategoryMappingRepository extends JpaRepository<ProductCategoryMapping, Long> {

	void deleteByProductId(Long productId);

	boolean existsByProductIdAndCategoryId(@Param("productId") Long productId, @Param("categoryId") Long categoryId);

	void deleteByProductIdAndCategoryId(@Param("productId") Long productId, @Param("categoryId") Long categoryId);

	List<ProductCategoryMapping> findByProductId(Long productId);

	List<ProductCategoryMapping> findByProductIdIn(List<Long> productIds);
}
