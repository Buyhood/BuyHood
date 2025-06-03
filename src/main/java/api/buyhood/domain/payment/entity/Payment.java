package api.buyhood.domain.payment.entity;

import api.buyhood.domain.order.entity.Order;
import api.buyhood.domain.order.enums.PaymentMethod;
import api.buyhood.domain.payment.enums.PGProvider;
import api.buyhood.domain.payment.enums.PayStatus;
import api.buyhood.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "payments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(name = "pg", columnDefinition = "VARCHAR(50)")
    private PGProvider pg;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", columnDefinition = "VARCHAR(50)")
    private PaymentMethod paymentMethod;

    @Column(nullable = false)
    private String buyerEmail;

    @Column(nullable = false)
    private long totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "pay_status", columnDefinition = "VARCHAR(50)")
    private PayStatus payStatus;

    @Column(nullable = false)
    private String merchantUid;

    @Builder
    public Payment (Order order, PGProvider pg, PaymentMethod paymentMethod, String buyerEmail, long totalPrice, String merchantUid ) {
        this.order = order;
        this.pg = pg;
        this.paymentMethod = paymentMethod;
        this.buyerEmail = buyerEmail;
        this.totalPrice = totalPrice;
        this.merchantUid = merchantUid;
        this.payStatus = PayStatus.READY;
    }

    public static Payment of(Order order, PGProvider pg, PaymentMethod paymentMethod, String buyerEmail, long totalPrice, String merchantUid) {
        return Payment.builder()
                .order(order)
                .pg(pg)
                .paymentMethod(paymentMethod)
                .buyerEmail(buyerEmail)
                .totalPrice(totalPrice)
                .merchantUid(merchantUid)
                .build();
    }

}
