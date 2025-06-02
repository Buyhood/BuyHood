package api.buyhood.domain.store.repository;

import api.buyhood.domain.store.entity.Store;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StoreRepository extends JpaRepository<Store, Long> {

	boolean existsByName(String storeName);

	@Query("select s from Store s where s.id = :storeId and s.deletedAt is null")
	Optional<Store> findActiveStoreById(Long storeId);

	@Query("select s from Store s inner join fetch s.seller where s.id = :storeId and s.deletedAt is null")
	Optional<Store> findActiveStoreByIdFetchSeller(Long storeId);

	@Query(
		value = "select s from Store s where s.deletedAt is null",
		countQuery = "select count(s) from Store s where s.deletedAt is null")
	Page<Store> findActiveStores(Pageable pageable);

	@Query(
		value = "select s from Store s where s.name like %:storeName% and s.deletedAt is null",
		countQuery = "select count(s) from Store s where s.name like %:storeName% and s.deletedAt is null")
	Page<Store> findActiveStoresByNameLike(@Param("storeName") String storeName, Pageable pageable);

}
