package api.buyhood.product.repository;

import api.buyhood.product.entity.Product;
import feign.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {

	@Query("select p from Product p where p.storeId = :storeId and p.deletedAt is null")
	List<Product> findActiveProductsByStoreId(Long storeId);

	@Query("select count(p) > 0 from Product p where p.storeId = :storeId and lower(p.name) = lower(:name)")
	boolean existsByStoreIdAndProductName(@Param("storeId") Long storeId, @Param("name") String productName);

	@Query("select p from Product p where p.id = :productId and p.deletedAt is null")
	Optional<Product> findActiveProductByProductId(Long productId);

	@Query("select count(p) > 0 from Product p where p.id = :productId and p.deletedAt is null")
	boolean existsActiveProductById(Long productId);

	@Query("select p from Product p where p.storeId = :storeId and p.deletedAt is null")
	Page<Product> findActiveProductsByStoreIdWithPaging(Long storeId, Pageable pageable);

	@Query("select p from Product p where p.storeId = :storeId and lower(p.name) like concat('%', lower(:keyword), '%') and p.deletedAt is null")
	Page<Product> findActiveProductByStoreIdAndKeyword(
		@Param("storeId") Long storeId,
		@Param("keyword") String keyword,
		Pageable pageable
	);
}
