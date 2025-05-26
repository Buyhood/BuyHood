package api.buyhood.domain.store.entity;

import api.buyhood.global.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "stores")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long id;

	@Column(nullable = false)
	private String storeName;

	@Column
	private String description;

	@Column(nullable = false)
	private String address;

	@Column
	private LocalDateTime openedAt;

	@Column
	private LocalDateTime closedAt;

	@Column
	private boolean isOpened;

	@Builder
	public Store(
		String storeName,
		String description,
		String address,
		LocalDateTime openedAt,
		LocalDateTime closedAt,
		boolean isOpened
	) {
		this.storeName = storeName;
		this.description = description;
		this.address = address;
		this.openedAt = openedAt;
		this.closedAt = closedAt;
		this.isOpened = isOpened;
	}
}
