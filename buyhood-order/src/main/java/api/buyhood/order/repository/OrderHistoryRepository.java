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
            "WHERE oh.order.id = :orderId " +
            "AND oh.order.userId = :userId")
    List<OrderHistory> findAllByOrderIdAndUserId(@Param("orderId") Long orderId, @Param("userId") Long userId);

    @Query("SELECT oh FROM OrderHistory oh " +
            "WHERE oh.order.userId = :userId")
    Page<OrderHistory> findAllByUserId(@Param("userId")Long userId,
                                       Pageable of
    );

    @Query("SELECT oh FROM OrderHistory oh " +
            "WHERE oh.order.storeId = :storeId "
    )
    Page<OrderHistory> findAllByStoreId(@Param("storeId")Long storeId,
                                        Pageable of
    );
}
