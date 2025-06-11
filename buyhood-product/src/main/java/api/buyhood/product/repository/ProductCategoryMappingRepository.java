package api.buyhood.product.repository;

import api.buyhood.product.entity.ProductCategoryMapping;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCategoryMappingRepository extends JpaRepository<ProductCategoryMapping, Long> {

	void deleteByProductId(Long productId);

	boolean existsByProductIdAndCategoryId(@Param("productId") Long productId, @Param("categoryId") Long categoryId);

	void deleteByProductIdAndCategoryId(@Param("productId") Long productId, @Param("categoryId") Long categoryId);
}
