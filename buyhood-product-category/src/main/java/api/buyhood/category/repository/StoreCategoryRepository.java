package api.buyhood.category.repository;

import api.buyhood.category.entity.StoreCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StoreCategoryRepository extends JpaRepository<StoreCategory, Long> {

	boolean existsByName(String name);

	@Query(
		value = "select sc from StoreCategory sc where sc.name like %:keyword%",
		countQuery = "select count(sc) from StoreCategory sc where sc.name like %:keyword%"
	)
	Page<StoreCategory> findByKeyword(String keyword, Pageable pageable);
}
