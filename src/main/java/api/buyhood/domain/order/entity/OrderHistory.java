package api.buyhood.domain.order.entity;

import api.buyhood.domain.product.entity.Product;
import api.buyhood.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Builder
    public OrderHistory (Order order, Product product, int quantity) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
    }

    public static OrderHistory of(Order order, Product product, int quantity) {
        return OrderHistory.builder()
                .order(order)
                .product(product)
                .quantity(quantity)
                .build();
    }
}
