package api.buyhood.domain.user.repository;

import api.buyhood.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InternalUserRepository extends JpaRepository<User, Long> {

	@Query("select u from User u where u.id = :userId and u.deletedAt is null")
	Optional<User> findActiveUserById(Long userId);
}
