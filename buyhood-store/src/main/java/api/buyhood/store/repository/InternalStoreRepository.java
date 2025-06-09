package api.buyhood.store.repository;

import api.buyhood.store.entity.Store;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InternalStoreRepository extends JpaRepository<Store, Long> {

	@Query("select s from Store s where s.id = :storeId and s.deletedAt is null")
	Optional<Store> findActiveStoreById(Long storeId);

	@Query("select count(s) > 0 from Store s where s.id = :storeId and s.deletedAt is null")
	Boolean existsActiveStoreById(Long storeId);
}
