package api.buyhood.domain.user.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChangeSellerRoleReq {

	@NotBlank(message = "사업자등록 번호를 기입해주세요.")
	private String businessNumber;
}
