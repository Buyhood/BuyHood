package api.buyhood.domain.seller.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DeleteSellerReq {

	@NotBlank
	private String password;
}
