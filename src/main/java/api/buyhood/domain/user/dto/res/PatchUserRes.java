package api.buyhood.domain.user.dto.res;

import api.buyhood.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PatchUserRes {

	private final String username;
	private final String address;

	public static PatchUserRes of(User user) {
		return new PatchUserRes(
			user.getUsername(),
			user.getAddress());
	}
}
