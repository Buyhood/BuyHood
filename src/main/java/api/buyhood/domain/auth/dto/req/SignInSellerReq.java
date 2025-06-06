package api.buyhood.domain.auth.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignInSellerReq {

	@NotBlank(message = "이메일은 필수 입력값입니다.")
	private String email;
	@NotBlank(message = "비밀번호는 필수 입력값입니다.")
	private String password;
}
