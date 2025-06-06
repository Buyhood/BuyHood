package api.buyhood.domain.payment.entity;

import api.buyhood.domain.order.entity.Order;
import api.buyhood.domain.payment.enums.PGProvider;
import api.buyhood.domain.payment.enums.PayStatus;
import api.buyhood.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static api.buyhood.domain.payment.enums.PayStatus.CANCELLED;
import static api.buyhood.domain.payment.enums.PayStatus.FAILED;
import static api.buyhood.domain.payment.enums.PayStatus.PAID;

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
	public Payment(Order order, PGProvider pg, String buyerEmail, BigDecimal totalPrice, String merchantUid) {
		this.order = order;
		this.pg = pg;
		this.buyerEmail = buyerEmail;
		this.totalPrice = totalPrice;
		this.merchantUid = merchantUid;
		this.payStatus = PayStatus.READY;
	}

	public static Payment of(Order order, PGProvider pg, String buyerEmail, BigDecimal totalPrice, String merchantUid) {
		return Payment.builder()
			.order(order)
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
