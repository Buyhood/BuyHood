package api.buyhood.domain.payment.repository;

import api.buyhood.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT p FROM Payment p " +
            "WHERE p.id = :paymentId " +
            "AND p.deletedAt is NULL")
    Optional<Payment> findNotDeletedById(@Param("paymentId") Long paymentId);

    @Query("SELECT p FROM Payment p " +
            "WHERE p.order.id = :orderId " +
            "AND p.deletedAt is NULL")
    Optional<Payment> findNotDeletedByOrderId(@Param("orderId") Long orderId);
}
