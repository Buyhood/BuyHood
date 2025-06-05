package api.buyhood.domain.product.repository;

import api.buyhood.domain.product.entity.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

	@Query(
		value = "select p from Product p join p.store s where s.id = :storeId and p.name like %:keyword% and p.deletedAt is null",
		countQuery = "select count(p) from Product p join p.store s where s.id = :storeId and p.name like %:keyword% and p.deletedAt is null")
	Page<Product> findActiveProductsByStoreIdAndKeyword(
		@Param("storeId") Long storeId,
		@Param("keyword") String keyword,
		Pageable pageable
	);

	@Query("select count(p) > 0 from Product p join p.store s where s.id = :storeId and p.name = :productName")
	boolean existsByStoreIdAndProductName(@Param("storeId") Long storeId, @Param("productName") String productName);

	@Query("select p from Product p join p.store s where s.id = :storeId and p.id = :productId and p.deletedAt is null")
	Optional<Product> findActiveProductByStoreIdAndProductId(
		@Param("storeId") Long storeId,
		@Param("productId") Long productId
	);

	@Query(
		value = "select p from Product p join p.store s where s.id = :storeId and p.deletedAt is null",
		countQuery = "select count(p) from Product p join p.store s where s.id = :storeId and p.deletedAt is null")
	Page<Product> findActiveProductsByStoreId(@Param("storeId") Long storeId, Pageable pageable);

	@Query("select p from Product p join p.store s where s.id = :storeId and p.deletedAt is null")
	List<Product> findActiveProductsByStoreId(Long storeId);
}
