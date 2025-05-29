package api.buyhood.domain.product.entity;

import api.buyhood.global.common.entity.BaseTimeEntity;
import api.buyhood.global.common.exception.InvalidRequestException;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static api.buyhood.global.common.exception.enums.ProductErrorCode.OUT_OF_STOCK;

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
	private String name;

	@Column(nullable = false)
	@Min(0)
	private Long price;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")
	private Category category;

	@Column
	private String description;

	@Column(nullable = false)
	private Long stock;

	@Builder
	public Product(String name, Long price, Category category, String description, Long stock) {
		this.name = name;
		this.price = price;
		this.category = category;
		this.description = description;
		this.stock = stock;
	}

	//재고 감소
	public void decreaseStock (int quantity) {

		if (stock < quantity) {
			throw new InvalidRequestException(OUT_OF_STOCK);
		}

		this.stock -= quantity;
	}
}
