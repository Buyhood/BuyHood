package api.buyhood.order.repository;

import api.buyhood.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InternalOrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o WHERE o.id = :orderId AND o.deletedAt is NULL")
    Optional<Order> findNotDeletedById(@Param("orderId") Long orderId);
}
