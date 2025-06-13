package api.buyhood.domain.user.dto.res;

import api.buyhood.domain.user.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PatchUserRes {

	private final String username;
	private final String address;

	public static PatchUserRes of(User user) {
		return new PatchUserRes(
			user.getUsername(),
			user.getAddress());
	}
}
