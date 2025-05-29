package api.buyhood.domain.order.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OrderHistoryRes {
    private Long orderId;
    private Long productId;
    private int quantity;
    private LocalDateTime createdAt;

    @Builder
    public OrderHistoryRes (Long orderId, Long productId, int quantity, LocalDateTime createdAt) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.createdAt = createdAt;
    }

    public static OrderHistoryRes of (Long orderId, Long productId, int quantity, LocalDateTime createdAt) {
        return OrderHistoryRes.builder()
                .orderId(orderId)
                .productId(productId)
                .quantity(quantity)
                .createdAt(createdAt)
                .build();
    }
}
