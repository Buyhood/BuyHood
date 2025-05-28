package api.buyhood.domain.user.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChangePasswordReq {

	@NotBlank
	private String oldPassword;
	@NotBlank
	private String newPassword;
}
