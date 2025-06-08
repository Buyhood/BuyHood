package api.buyhood.store.repository;

import api.buyhood.store.entity.Store;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StoreRepository extends JpaRepository<Store, Long> {

	@Query("select count(s) > 0 from Store s where lower(s.name) = lower(:name)")
	boolean existsAllByName(String name);

	@Query("select s from Store s where s.id = :storeId and s.deletedAt is null")
	Optional<Store> findActiveStoreById(Long storeId);

	@Query(
		value = "select s from Store s where s.deletedAt is null",
		countQuery = "select count(s) from Store s where s.deletedAt is null"
	)
	Page<Store> findActiveStores(Pageable pageable);

	@Query(
		value = "select s from Store s where s.storeCategoryId = :storeCategoryId and s.deletedAt is null",
		countQuery = "select count(s) from Store s where s.storeCategoryId = :storeCategoryId and s.deletedAt is null"
	)
	Page<Store> findActiveStoresByStoreCategoryId(@Param("storeCategoryId") Long storeCategoryId, Pageable pageable);
}
