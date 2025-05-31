package api.buyhood.domain.user.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChangeUserPasswordReq {

	@NotBlank(message = "기존 비밀번호는 필수 입력값입니다.")
	private String oldPassword;
	@NotBlank(message = "새 비밀번호는 필수 입력값입니다.")
	@Pattern(
		regexp = "^(?=.*[A-Z])(?=.*\\d).{8,}$",
		message = "비밀번호는 8자 이상, 숫자와 대문자를 포함해야 합니다."
	)
	private String newPassword;
}
