package api.buyhood.domain.order.entity;

import api.buyhood.domain.order.enums.OrderStatus;
import api.buyhood.global.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long id;

	@Column
	private String paymentMethod;

	@Column(nullable = false)
	private Long totalPrice;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private OrderStatus status;

	@Column
	private LocalDateTime pickupAt;

	@Builder
	public Order(String paymentMethod, Long totalPrice, OrderStatus status, LocalDateTime pickupAt) {
		this.paymentMethod = paymentMethod;
		this.totalPrice = totalPrice;
		this.status = status;
		this.pickupAt = pickupAt;
	}
}
