package api.buyhood.domain.seller.repository;

import api.buyhood.domain.seller.entity.Seller;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SellerRepository extends JpaRepository<Seller, Long> {

	boolean existsByEmail(String email);

	@Query("SELECT s FROM Seller s WHERE s.email = :email AND s.deletedAt IS NULL")
	Optional<Seller> findByEmail(@Param("email") String email);

	@Query("SELECT s FROM Seller s WHERE s.deletedAt IS NULL ")
	Page<Seller> findAllActiveSellers(Pageable pageable);
}
