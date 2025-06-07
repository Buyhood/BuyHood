package api.buyhood.storecategory.repository;

import api.buyhood.storecategory.entity.StoreCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StoreCategoryRepository extends JpaRepository<StoreCategory, Long> {

	boolean existsByName(String name);

	@Query(
		value = "select sc from StoreCategory sc where lower(sc.name) like concat('%', lower(:keyword), '%')",
		countQuery = "select count(sc) from StoreCategory sc where lower(sc.name) like concat('%', lower(:keyword), '%')"
	)
	Page<StoreCategory> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
