package api.buyhood.domain.seller.repository;

import api.buyhood.domain.seller.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<Seller, Long> {

	boolean existsByEmail(String email);
}
