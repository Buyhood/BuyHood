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
    private String orderState; //todo: enum으로 받아오도록 수정해보기
    private String requestMessage;
    private LocalTime readyAt;
}
