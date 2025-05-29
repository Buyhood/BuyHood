package api.buyhood.domain.user.entity;

import api.buyhood.domain.user.enums.UserRole;
import api.buyhood.global.common.entity.BaseTimeEntity;
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
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

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
	@Enumerated(EnumType.STRING)
	private UserRole role;

	@Column(nullable = false)
	private String address;

	@Builder
	public User(String username, String email, String password, String address) {
		this.username = username;
		this.email = email;
		this.password = password;
		this.role = UserRole.USER;
		this.address = address;
	}

	public void changePassword(String encodedPassword) {
		this.password = encodedPassword;
	}

	public void deleteUser() {
		this.markDeleted();
	}

	public void patchUser(String username, String address) {
		if (username != null) {
			this.username = username;
		}
		if (address != null) {
			this.address = address;
		}
	}
}
