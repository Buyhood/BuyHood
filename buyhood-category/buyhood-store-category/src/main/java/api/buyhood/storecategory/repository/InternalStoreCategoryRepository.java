package api.buyhood.storecategory.repository;

import api.buyhood.storecategory.entity.StoreCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InternalStoreCategoryRepository extends JpaRepository<StoreCategory, Long> {

	Optional<StoreCategory> findByName(String storeCategoryName);

	@Query("select sc from StoreCategory sc where sc.id in :storeCategoryIds")
	List<StoreCategory> findByIds(List<Long> storeCategoryIds);
}
