package api.buyhood.domain.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
	USER("ROLE_USER"),
	SELLER("ROLE_SELLER");

	private final String role;

	// 필요 시 of 메서드 구현
}
