package api.buyhood.domain.user.dto.res;

import api.buyhood.domain.user.entity.User;
import api.enums.UserRole;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GetUserRes {

	private final String username;
	private final String email;
	//TODO:역할이 여러개일 경우 리스트에 담아서 내야할까?
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
