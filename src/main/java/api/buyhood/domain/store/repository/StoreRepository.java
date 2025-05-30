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

	@Query("select s from Store s inner join fetch s.seller where s.id = :storeId")
	Optional<Store> findStoreById(Long storeId);

	@Query(
		value = "select s from Store s inner join fetch s.seller",
		countQuery = "select count(s) from Store s")
	Page<Store> findFetchAll(Pageable pageable);

	@Query(
		value = "select s from Store s inner join fetch s.seller where s.name like %:storeName%",
		countQuery = "select count(s) from Store s where s.name like %:storeName%")
	Page<Store> findByKeyword(@Param("storeName") String storeName, Pageable pageable);

}
