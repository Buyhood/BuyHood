package api.buyhood.domain.product.entity;

import api.buyhood.entity.BaseTimeEntity;
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
@Table(name = "product_categories_mapping")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductCategoryMapping extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long id;

	@Column(nullable = false)
	private Long productId;

	@Column(nullable = false)
	private Long categoryId;

	@Builder
	public ProductCategoryMapping(Long productId, Long categoryId) {
		this.productId = productId;
		this.categoryId = categoryId;
	}
}
