package api.buyhood.domain.user.repository;

import api.buyhood.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

	boolean existsByEmail(String email);

	@Query("SELECT u FROM User u WHERE u.email = :email AND u.deletedAt IS NULL")
	Optional<User> findByEmail(@Param("email") String email);

	@Query("SELECT u FROM User u WHERE u.deletedAt IS NULL ")
	Page<User> findAllActiveUsers(Pageable pageable);

}
