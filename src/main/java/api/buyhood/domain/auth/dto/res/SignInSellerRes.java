package api.buyhood.domain.auth.dto.res;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SignInSellerRes {

	private final String token;
}
