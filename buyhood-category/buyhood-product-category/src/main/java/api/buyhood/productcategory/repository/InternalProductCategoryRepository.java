package api.buyhood.productcategory.repository;

import api.buyhood.productcategory.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InternalProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

}
