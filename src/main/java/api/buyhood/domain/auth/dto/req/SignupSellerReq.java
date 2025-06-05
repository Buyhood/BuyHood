package api.buyhood.domain.auth.dto.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupSellerReq {

	@Email
	@NotBlank(message = "이메일은 필수입력 값입니다.")
	private String email;
	@NotBlank(message = "비밀번호는 필수입력 값입니다.")
	private String password;
	@NotBlank(message = "유저이름은 필수입력 값입니다.")
	private String username;
	@NotBlank(message = "사업자번호는 필수입력 값입니다.")
	private String businessNumber;
	@NotBlank(message = "휴대폰번호는 필수 입력 값입니다.")
	private String phoneNumber;
}
