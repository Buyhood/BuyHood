package api.buyhood.domain.payment.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ApplyPaymentReq {
    private final String pg;
    private final String name;
    private final String paymentMethod;
    private final String merchantUid;
    private final BigDecimal totalPrice;
    private final String buyerEmail;

    @Builder
    public ApplyPaymentReq(String pg,  String name, String paymentMethod, String merchantUid, BigDecimal totalPrice, String buyerEmail) {
        this.pg = pg;
        this.name = name;
        this.paymentMethod = paymentMethod;
        this.merchantUid = merchantUid;
        this.totalPrice = totalPrice;
        this.buyerEmail = buyerEmail;
    }

    public static ApplyPaymentReq of (String pg,  String name,String paymentMethod, String merchantUid, BigDecimal totalPrice, String buyerEmail) {
        return ApplyPaymentReq.builder()
                .pg(pg)
                .name(name)
                .paymentMethod(paymentMethod)
                .merchantUid(merchantUid)
                .totalPrice(totalPrice)
                .buyerEmail(buyerEmail)
                .build();
    }
}
