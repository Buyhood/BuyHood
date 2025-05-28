package api.buyhood.domain.auth.dto.res;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SignInUserRes {

	private final String token;

}
