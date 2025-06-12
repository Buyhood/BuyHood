package api.buyhood.dto.payment;

import lombok.*;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PaymentFeignDto {
    private Long paymentId;
    private Long orderId;
    private String pg;
    private String buyerEmail;
    private BigDecimal totalPrice;
    private String payStatus;
    private String merchantUid;
}
