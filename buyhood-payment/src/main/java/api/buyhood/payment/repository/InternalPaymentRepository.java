package api.buyhood.payment.repository;

import api.buyhood.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InternalPaymentRepository extends JpaRepository<Payment, Long> {
    @Query("SELECT p FROM Payment p " +
            "WHERE p.orderId = :orderId " +
            "AND p.deletedAt is NULL")
    Optional<Payment> findNotDeletedByOrderId(@Param("orderId") Long orderId);

    @Query("SELECT p FROM Payment p " +
            "WHERE p.id = :paymentId " +
            "AND p.deletedAt is NULL")
    Optional<Payment> findNotDeletedById(@Param("paymentId")Long paymentId);
}
