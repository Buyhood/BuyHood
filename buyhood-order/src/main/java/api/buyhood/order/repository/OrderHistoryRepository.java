package api.buyhood.order.repository;

import api.buyhood.order.entity.OrderHistory;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long> {
    @Query("SELECT oh FROM OrderHistory oh " +
            "JOIN FETCH oh.order " +
            "JOIN FETCH oh.product " +
            "WHERE oh.order.id = :orderId")
    List<OrderHistory> findAllByOrderId(@Param("orderId") Long orderId);

    @Query("SELECT oh FROM OrderHistory oh " +
            "WHERE oh.order.user.id = :userId")
    Page<OrderHistory> findAllByUserId(@Param("userId")Long userId,
                                       Pageable of
    );

    @Query("SELECT oh FROM OrderHistory oh " +
            "WHERE oh.order.store.id = :storeId "
    )
    Page<OrderHistory> findAllByStoreId(@Param("storeId")Long storeId,
                                        Pageable of
    );
}
