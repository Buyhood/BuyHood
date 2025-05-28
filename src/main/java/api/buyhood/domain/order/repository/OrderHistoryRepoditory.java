package api.buyhood.domain.order.repository;

import api.buyhood.domain.order.entity.OrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderHistoryRepoditory extends JpaRepository<OrderHistory, Long> {
}
