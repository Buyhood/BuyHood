package api.buyhood.domain.payment.dto.response;

import api.buyhood.domain.payment.enums.PGProvider;
import api.buyhood.domain.payment.enums.PayStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public class PaymentRes {
    private final Long paymentId;
    private final Long orderId;
    private final PGProvider pg;
    private final String buyerEmail;
    private final BigDecimal totalPrice;
    private final PayStatus payStatus;

    public static PaymentRes of(Long paymentId, Long orderId, PGProvider pg, String buyerEmail, BigDecimal totalPrice, PayStatus payStatus) {
        return new PaymentRes (
                paymentId,
                orderId,
                pg,
                buyerEmail,
                totalPrice,
                payStatus
        );
    }
}
