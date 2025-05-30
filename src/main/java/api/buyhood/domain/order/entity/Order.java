package api.buyhood.domain.order.entity;

import api.buyhood.domain.order.enums.OrderStatus;
import api.buyhood.domain.order.enums.PaymentMethod;
import api.buyhood.domain.store.entity.Store;
import api.buyhood.domain.user.entity.User;
import api.buyhood.global.common.entity.BaseTimeEntity;
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
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
	@JoinColumn(name = "store_id")
	private Store store;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private PaymentMethod paymentMethod;

	@Column(nullable = false)
	private long totalPrice;

	@Column
	@Enumerated(EnumType.STRING)
	private OrderStatus status;

	@Column
	private String requestMessage;

	@Column
	private LocalTime readyAt;

	@Builder
	public Order(Store store, User user, String requestMessage, PaymentMethod paymentMethod, long totalPrice,
		OrderStatus status,
		LocalTime readyAt) {
		this.store = store;
		this.user = user;
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
