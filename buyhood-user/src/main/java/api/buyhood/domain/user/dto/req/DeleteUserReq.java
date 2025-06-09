package api.buyhood.domain.user.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DeleteUserReq {

	@NotBlank(message = "비밀번호를 입력하세요")
	private String password;

}
