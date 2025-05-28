package api.buyhood.domain.order.dto.response;

import api.buyhood.domain.cart.dto.response.CartRes;
import api.buyhood.domain.order.enums.OrderStatus;
import api.buyhood.domain.order.enums.PaymentMethod;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OrderRes {
    private CartRes orderInfo;
    private long totalPrice;
    private PaymentMethod paymentMethod;
    private OrderStatus status;
    private LocalDateTime pickupAt;

    @Builder
    private OrderRes (CartRes orderInfo,long totalPrice, PaymentMethod paymentMethod, OrderStatus status, LocalDateTime pickupAt) {
        this.orderInfo = orderInfo;
        this.totalPrice = totalPrice;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.pickupAt = pickupAt;
    }

    public static OrderRes of (CartRes orderInfo, long totalPrice, PaymentMethod paymentMethod, OrderStatus status, LocalDateTime pickupAt) {
        return OrderRes.builder()
                .orderInfo(orderInfo)
                .totalPrice(totalPrice)
                .paymentMethod(paymentMethod)
                .status(status)
                .pickupAt(pickupAt)
                .build();
    }
}
