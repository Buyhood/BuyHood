package api.buyhood.payment.entity;

import api.buyhood.entity.BaseTimeEntity;
import api.buyhood.payment.enums.PGProvider;
import api.buyhood.payment.enums.PayStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static api.buyhood.payment.enums.PayStatus.*;


@Getter
@Entity
@Table(name = "payments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long id;

	@Column(nullable = false)
	private Long orderId;

	@Enumerated(EnumType.STRING)
	@Column(name = "pg", columnDefinition = "VARCHAR(50)")
	private PGProvider pg;

	@Column(nullable = false)
	private String buyerEmail;

	@Column(nullable = false)
	private BigDecimal totalPrice;

	@Enumerated(EnumType.STRING)
	@Column(name = "pay_status", columnDefinition = "VARCHAR(50)")
	private PayStatus payStatus;

	@Column(nullable = false, unique = true)
	private String merchantUid;

	@Builder
	public Payment(Long orderId, PGProvider pg, String buyerEmail, BigDecimal totalPrice, String merchantUid) {
		this.orderId = orderId;
		this.pg = pg;
		this.buyerEmail = buyerEmail;
		this.totalPrice = totalPrice;
		this.merchantUid = merchantUid;
		this.payStatus = PayStatus.READY;
	}

	public static Payment of(Long orderId, PGProvider pg, String buyerEmail, BigDecimal totalPrice, String merchantUid) {
		return Payment.builder()
			.orderId(orderId)
			.pg(pg)
			.buyerEmail(buyerEmail)
			.totalPrice(totalPrice)
			.merchantUid(merchantUid)
			.build();
	}

	public void successPayment() {
		this.payStatus = PAID;
	}

	public void failPayment() {
		this.payStatus = FAILED;
	}

	public boolean isPaid() {
		return PAID.equals(this.payStatus);
	}

	public void cancel() {
		this.payStatus = CANCELLED;
	}
}
