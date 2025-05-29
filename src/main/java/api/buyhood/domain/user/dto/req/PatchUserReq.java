package api.buyhood.domain.user.dto.req;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PatchUserReq {

	private String username;
	private String address;
}
