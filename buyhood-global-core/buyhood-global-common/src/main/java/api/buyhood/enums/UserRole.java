package api.buyhood.enums;

import api.buyhood.errorcode.UserErrorCode;
import api.buyhood.exception.AuthenticationException;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
	USER("ROLE_USER"),
	ADMIN("ROLE_ADMIN"),
	SELLER("ROLE_SELLER");

	private final String role;

	public static UserRole of(String role) {
		return Arrays.stream(UserRole.values())
			.filter(r -> r.name().equalsIgnoreCase(role) || r.getRole().equalsIgnoreCase(role))
			.findFirst()
			.orElseThrow(() -> new AuthenticationException(UserErrorCode.INVALID_USER_ROLE));
	}

}
