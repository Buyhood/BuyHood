package api.buyhood.domain.product.entity;

import api.buyhood.domain.store.entity.Store;
import api.buyhood.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
	private String name;

	@Column(nullable = false)
	private Long price;

	@Column
	private String description;

	@Column(nullable = false)
	private Long stock;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id")
	private Store store;

	@Builder
	public Product(String name, Long price, String description, Long stock, Store store) {
		this.name = name;
		this.price = price;
		this.description = description;
		this.stock = stock;
		this.store = store;
	}

	public void decreaseStock(int quantity) {
		this.stock -= quantity;
	}

	//재고 롤백
	public void rollBackStock(int quantity) {
		this.stock += quantity;
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
