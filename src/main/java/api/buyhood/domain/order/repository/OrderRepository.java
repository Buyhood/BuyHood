package api.buyhood.domain.order.repository;

import api.buyhood.domain.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o WHERE o.id = :orderId AND o.deletedAt is NULL")
    Optional<Order> findNotDeletedById(@Param("orderId") Long orderId);

    @Query("SELECT o FROM Order o WHERE o.deletedAt is NULL")
    Page<Order> findNotDeletedAll(Pageable pageable);
}
