package api.buyhood.domain.seller.entity;

import api.entity.BaseTimeEntity;
import api.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
	private String phoneNumber;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private UserRole role;

	@Builder
	public Seller(String username, String email, String password, String businessNumber, String phoneNumber
	) {
		this.username = username;
		this.email = email;
		this.password = password;
		this.businessNumber = businessNumber;
		this.phoneNumber = phoneNumber;
		this.role = UserRole.SELLER;
	}

	public void changePassword(String encodedPassword) {
		this.password = encodedPassword;
	}

	public void deleteSeller() {
		this.markDeleted();
	}

}
