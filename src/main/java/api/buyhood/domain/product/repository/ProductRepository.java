package api.buyhood.domain.product.repository;

import api.buyhood.domain.product.entity.Product;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

	@Query(
		value = "select p from Product p where p.name like %:keyword%",
		countQuery = "select count(p) from Product p where p.name like %:keyword%")
	Page<Product> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

	@Query("select count(p) > 0 from Product p where p.name = :productName")
	boolean existsByName(String productName);

	@Query("select p from Product p where p.id = :productId and p.deletedAt is null")
	Optional<Product> findByIdAndDeletedAtIsNull(@Param("productId") Long productId);
}
