package api.buyhood.domain.payment.dto.response;

import api.buyhood.domain.order.enums.PaymentMethod;
import api.buyhood.domain.payment.enums.PGProvider;
import api.buyhood.domain.payment.enums.PayStatus;
import lombok.Builder;
import lombok.Getter;

@Getter

public class PaymentRes {
    private Long paymentId;
    private Long orderId;
    private PGProvider pg;
    private PaymentMethod paymentMethod;
    private String buyerEmail;
    private long totalPrice;
    private PayStatus payStatus;

    @Builder
    public PaymentRes(Long paymentId, Long orderId, PGProvider pg, PaymentMethod paymentMethod, String buyerEmail, long totalPrice, PayStatus payStatus) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.pg = pg;
        this.paymentMethod = paymentMethod;
        this.buyerEmail = buyerEmail;
        this.totalPrice = totalPrice;
        this.payStatus = payStatus;
    }

    public static PaymentRes of(Long paymentId, Long orderId, PGProvider pg, PaymentMethod paymentMethod, String buyerEmail, long totalPrice, PayStatus payStatus) {
        return PaymentRes.builder()
                .paymentId(paymentId)
                .orderId(orderId)
                .pg(pg)
                .paymentMethod(paymentMethod)
                .buyerEmail(buyerEmail)
                .totalPrice(totalPrice)
                .payStatus(payStatus)
                .build();
    }
}
