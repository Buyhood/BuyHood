package api.buyhood.domain.store.entity;

import api.buyhood.domain.seller.entity.Seller;
import api.buyhood.global.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "stores")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String address;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seller_id")
	private Seller seller;

	@Column(nullable = false)
	private boolean isDeliverable;

	@Column
	private String description;

	@Column
	private LocalTime openedAt;

	@Column
	private LocalTime closedAt;


	@Builder
	public Store(
		String name,
		String address,
		Seller seller,
		boolean isDeliverable,
		String description,
		LocalTime openedAt,
		LocalTime closedAt
	) {
		this.name = name;
		this.address = address;
		this.seller = seller;
		this.isDeliverable = isDeliverable;
		this.description = description;
		this.openedAt = openedAt;
		this.closedAt = closedAt;
	}

	public void patchName(String storeName) {
		this.name = storeName;
	}

	public void patchAddress(String address) {
		this.address = address;
	}

	public void patchSeller(Seller seller) {
		this.seller = seller;
	}

	public void patchIsDeliverable(boolean isDeliverable) {
		this.isDeliverable = isDeliverable;
	}

	public void patchDescription(String description) {
		this.description = description;
	}

	public void patchOpenedAt(LocalTime openedAt) {
		this.openedAt = openedAt;
	}

	public void patchClosedAt(LocalTime closedAt) {
		this.closedAt = closedAt;
	}
}
