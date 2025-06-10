package api.buyhood.order.entity;

import api.buyhood.domain.user.entity.User;
import api.buyhood.entity.BaseTimeEntity;
import api.buyhood.order.enums.OrderStatus;
import api.buyhood.order.enums.PaymentMethod;
import api.buyhood.store.entity.Store;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Getter
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseTimeEntity {

	//(온라인 결제) 후 Apply , Accept(배송이나 픽업 예상시간 보내기, 사업자 전용 API) , Reject(환불)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id")
	private Store store;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private PaymentMethod paymentMethod;

	@Column(nullable = false)
	private BigDecimal totalPrice;

	@Column
	@Enumerated(EnumType.STRING)
	private OrderStatus status;

	@Column
	private String requestMessage;

	@Column
	private LocalTime readyAt;

	@Builder
	public Order(Store store, User user, String name, String requestMessage, PaymentMethod paymentMethod,
		BigDecimal totalPrice,
		OrderStatus status,
		LocalTime readyAt) {
		this.store = store;
		this.user = user;
		this.name = name;
		this.requestMessage = requestMessage;
		this.paymentMethod = paymentMethod;
		this.totalPrice = totalPrice;
		this.status = status;
		this.readyAt = readyAt;
	}

	public void accept(LocalTime readyAt) {
		this.status = OrderStatus.ACCEPTED;
		this.readyAt = readyAt;
	}

	public void reject() {
		this.status = OrderStatus.REJECTED;
	}

	public void delete() {
		markDeleted();
		this.status = OrderStatus.CANCELED;
	}
}
