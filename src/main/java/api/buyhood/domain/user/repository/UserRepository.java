package api.buyhood.domain.user.repository;

import api.buyhood.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

	boolean existsByEmail(String email);

	@Query("SELECT u FROM User u WHERE u.deletedAt IS NULL")
	Optional<User> findByEmail(String email);
}
