package api.buyhood.domain.seller.entity;

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
@Table(name = "sellers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Seller extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long id;

	@Column(nullable = false)
	private String username;

	@Column(nullable = false)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String businessNumber;

	@Column(nullable = false)
	private String businessName;

	@Column(nullable = false)
	private String address;

	@Builder
	public Seller(
		String username,
		String email,
		String password,
		String businessNumber,
		String businessName,
		String address
	) {
		this.username = username;
		this.email = email;
		this.password = password;
		this.businessNumber = businessNumber;
		this.businessName = businessName;
		this.address = address;
	}
}
