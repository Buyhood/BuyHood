package api.buyhood.domain.product.entity;

import api.buyhood.global.common.entity.BaseTimeEntity;
import api.buyhood.global.common.exception.InvalidRequestException;
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
	private Long price;

	@Column
	private String description;

	@Column(nullable = false)
	private Long stock;

	@Builder
	public Product(String name, Long price, String description, Long stock) {
		this.name = name;
		this.price = price;
		this.description = description;
		this.stock = stock;
	}

	//재고 감소
	public void decreaseStock(int quantity) {

		if (stock < quantity) {
			throw new InvalidRequestException(OUT_OF_STOCK);
		}

		this.stock -= quantity;
	}

	public void patchName(String productName) {
		this.name = productName;
	}

	public void patchPrice(Long price) {
		this.price = price;
	}

	public void patchDescription(String description) {
		this.description = description;
	}

	public void patchStock(Long stock) {
		this.stock = stock;
	}
}
