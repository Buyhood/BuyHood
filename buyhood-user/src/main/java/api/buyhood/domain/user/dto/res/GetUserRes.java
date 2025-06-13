package api.buyhood.domain.user.dto.res;

import api.buyhood.domain.user.entity.User;
import api.buyhood.enums.UserRole;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GetUserRes {

	private final String username;
	private final String email;

	private final UserRole role;
	private final String address;

	public static GetUserRes of(User user) {
		return new GetUserRes(
			user.getUsername(),
			user.getEmail(),
			user.getRole(),
			user.getAddress()
		);
	}
}
