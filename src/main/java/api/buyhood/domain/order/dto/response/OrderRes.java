package api.buyhood.domain.order.dto.response;

import api.buyhood.domain.order.entity.Order;
import api.buyhood.domain.order.enums.OrderStatus;
import api.buyhood.domain.order.enums.PaymentMethod;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OrderRes {

    private Long orderId;
    private PaymentMethod paymentMethod;
    private long totalPrice;
    private OrderStatus status;
    private LocalDateTime pickupAt;

    @Builder
    public OrderRes(Long orderId, PaymentMethod paymentMethod, long totalPrice, OrderStatus status, LocalDateTime pickupAt ) {
        this.orderId = orderId;
        this.paymentMethod = paymentMethod;
        this.totalPrice = totalPrice;
        this.status = status;
        this.pickupAt = pickupAt;
    }

    public static OrderRes of(Order order) {
        return OrderRes.builder()
                .orderId(order.getId())
                .paymentMethod(order.getPaymentMethod())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .pickupAt(order.getPickupAt())
                .build();
    }
}
