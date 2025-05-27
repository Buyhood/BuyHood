package api.buyhood.domain.user.enums;

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
			.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 UserRole: " + role));
	}

	public static class Authority {

		public static final String USER = "ROLE_USER";
		public static final String SELLER = "ROLE_SELLER";

	}
}
