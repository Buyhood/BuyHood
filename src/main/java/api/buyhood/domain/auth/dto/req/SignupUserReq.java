package api.buyhood.domain.auth.dto.req;

import api.buyhood.domain.user.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupUserReq {

	@NotBlank
	@Email
	private String email;
	@NotBlank
	private String password;
	private String username;
	private String address;
	private UserRole role;

}
