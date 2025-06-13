package api.buyhood.product.repository;

import api.buyhood.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InternalProductRepository extends JpaRepository<Product, Long> {
}
