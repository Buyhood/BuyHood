package api.buyhood.domain.order.dto.response;

import api.buyhood.domain.cart.dto.response.CartRes;
import api.buyhood.domain.order.enums.OrderStatus;
import api.buyhood.domain.order.enums.PaymentMethod;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CreateOrderRes {
    private CartRes orderInfo;
    private long totalPrice;
    private PaymentMethod paymentMethod;
    private OrderStatus status;
    private LocalDateTime pickupAt;

    @Builder
    private CreateOrderRes(CartRes orderInfo, long totalPrice, PaymentMethod paymentMethod, OrderStatus status, LocalDateTime pickupAt) {
        this.orderInfo = orderInfo;
        this.totalPrice = totalPrice;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.pickupAt = pickupAt;
    }

    public static CreateOrderRes of (CartRes orderInfo, long totalPrice, PaymentMethod paymentMethod, OrderStatus status, LocalDateTime pickupAt) {
        return CreateOrderRes.builder()
                .orderInfo(orderInfo)
                .totalPrice(totalPrice)
                .paymentMethod(paymentMethod)
                .status(status)
                .pickupAt(pickupAt)
                .build();
    }
}
