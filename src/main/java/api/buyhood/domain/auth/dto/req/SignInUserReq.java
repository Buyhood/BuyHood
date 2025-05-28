package api.buyhood.domain.auth.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignInUserReq {

	@NotBlank
	private String email;
	@NotBlank
	private String password;

}
