package api.buyhood.auth.dto.res;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SignupUserRes {

	private final String token;
}
