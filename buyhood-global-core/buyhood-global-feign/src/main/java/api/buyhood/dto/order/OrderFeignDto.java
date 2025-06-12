package api.buyhood.dto.order;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OrderFeignDto {
    private Long orderId;
    private Long userId;
    private Long storeId;
    private String name;
    private String paymentMethod;
    private BigDecimal totalPrice;
    private String orderState;
    private String requestMessage;
    private LocalTime readyAt;
}
