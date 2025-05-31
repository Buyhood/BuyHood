package api.buyhood.domain.seller.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DeleteSellerReq {

	@NotBlank(message = "비밀번호를 입력해주세요")
	private String password;
}
