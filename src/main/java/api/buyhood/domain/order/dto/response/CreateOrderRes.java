package api.buyhood.domain.order.dto.response;

import api.buyhood.domain.cart.dto.response.CartRes;
import api.buyhood.domain.order.enums.OrderStatus;
import api.buyhood.domain.order.enums.PaymentMethod;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CreateOrderRes {
    private Long storeId;
    private CartRes orderInfo;
    private long totalPrice;
    private PaymentMethod paymentMethod;
    private OrderStatus status;
    private LocalDateTime pickupAt;
    private LocalDateTime createAt;

    @Builder
    private CreateOrderRes(Long storeId, CartRes orderInfo, long totalPrice, PaymentMethod paymentMethod, OrderStatus status, LocalDateTime pickupAt, LocalDateTime createAt) {
        this.storeId = storeId;
        this.orderInfo = orderInfo;
        this.totalPrice = totalPrice;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.pickupAt = pickupAt;
        this.createAt = createAt;
    }

    public static CreateOrderRes of (Long storeId, CartRes orderInfo, long totalPrice, PaymentMethod paymentMethod, OrderStatus status, LocalDateTime pickupAt, LocalDateTime createAt) {
        return CreateOrderRes.builder()
                .storeId(storeId)
                .orderInfo(orderInfo)
                .totalPrice(totalPrice)
                .paymentMethod(paymentMethod)
                .status(status)
                .pickupAt(pickupAt)
                .createAt(createAt)
                .build();
    }
}
