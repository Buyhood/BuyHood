package api.buyhood.auth.dto.req;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupUserReq {


	@Email
	@NotBlank(message = "이메일은 필수입력 값입니다.")
	private String email;
	@NotBlank(message = "비밀번호는 필수입력 값입니다.")
	private String password;
	@NotBlank(message = "유저 이름은 필수입력 값입니다.")
	private String username;
	@NotBlank(message = "주소는 필수 입력 값입니다.")
	private String address;
	@NotBlank(message = "휴대폰 번호는 필수 입력 값입니다.")
	private String phoneNumber;

}
