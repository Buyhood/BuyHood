package api.buyhood.domain.order.entity;

import api.buyhood.domain.order.enums.OrderStatus;
import api.buyhood.domain.order.enums.PaymentMethod;
import api.buyhood.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long id;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private PaymentMethod paymentMethod;

	@Column(nullable = false)
	private long totalPrice;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private OrderStatus status;

	@Column
	private LocalDateTime pickupAt;

	@Builder
	public Order(PaymentMethod paymentMethod, long totalPrice, LocalDateTime pickupAt) {
		this.paymentMethod = paymentMethod;
		this.totalPrice = totalPrice;
		this.status = OrderStatus.PENDING;
		this.pickupAt = pickupAt;
	}

	public static Order of(PaymentMethod paymentMethod, long totalPrice, LocalDateTime pickupAt) {
		return Order.builder()
				.paymentMethod(paymentMethod)
				.totalPrice(totalPrice)
				.pickupAt(pickupAt)
				.build();
	}

	public void delete() {
		markDeleted();
		this.status = OrderStatus.CANCELED;
	}
}
