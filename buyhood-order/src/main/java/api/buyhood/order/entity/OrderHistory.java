package api.buyhood.order.entity;

import api.buyhood.entity.BaseTimeEntity;
import api.buyhood.product.entity.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "order_histories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderHistory extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	private Order order;

	@Column(nullable = false)
	private Long productId;

	@Column(nullable = false)
	private int quantity;

	@Builder
	public OrderHistory(Order order, Long productId, int quantity) {
		this.order = order;
		this.productId = productId;
		this.quantity = quantity;
	}

}
