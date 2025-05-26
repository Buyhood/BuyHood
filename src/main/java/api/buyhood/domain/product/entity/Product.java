package api.buyhood.domain.product.entity;

import api.buyhood.global.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "products")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long id;

	@Column(nullable = false)
	private String productName;

	@Column(nullable = false)
	private Long price;

	@Column
	private String category;

	@Column
	private String description;

	@Column(nullable = false)
	private Long stock;

	@Builder
	public Product(String productName, Long price, String category, String description, Long stock) {
		this.productName = productName;
		this.price = price;
		this.category = category;
		this.description = description;
		this.stock = stock;
	}
}
