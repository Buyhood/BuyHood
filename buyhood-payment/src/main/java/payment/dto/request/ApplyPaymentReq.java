package payment.dto.request;

import api.buyhood.domain.order.enums.PaymentMethod;
import payment.enums.PGProvider;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ApplyPaymentReq {
    private final PGProvider pg;
    private final PaymentMethod paymentMethod;
    private final String merchantUid;
    private final BigDecimal totalPrice;
    private final String buyerEmail;

    @Builder
    public ApplyPaymentReq(PGProvider pg, PaymentMethod paymentMethod, String merchantUid,  BigDecimal totalPrice, String buyerEmail) {
        this.pg = pg;
        this.paymentMethod = paymentMethod;
        this.merchantUid = merchantUid;
        this.totalPrice = totalPrice;
        this.buyerEmail = buyerEmail;
    }

    public static ApplyPaymentReq of (PGProvider pg, PaymentMethod paymentMethod, String merchantUid, BigDecimal totalPrice, String buyerEmail) {
        return ApplyPaymentReq.builder()
                .pg(pg)
                .paymentMethod(paymentMethod)
                .merchantUid(merchantUid)
                .totalPrice(totalPrice)
                .buyerEmail(buyerEmail)
                .build();
    }
}
