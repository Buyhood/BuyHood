package api.buyhood.domain.user.enums;

import api.buyhood.global.common.exception.AuthenticationException;
import api.buyhood.global.common.exception.enums.UserErrorCode;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
	USER("ROLE_USER"),
	SELLER("ROLE_SELLER");

	private final String role;

	public static UserRole of(String role) {
		return Arrays.stream(UserRole.values())
			.filter(r -> r.name().equalsIgnoreCase(role) || r.getRole().equalsIgnoreCase(role))
			.findFirst()
			.orElseThrow(() -> new AuthenticationException(UserErrorCode.INVALID_USER_ROLE));
	}

}
