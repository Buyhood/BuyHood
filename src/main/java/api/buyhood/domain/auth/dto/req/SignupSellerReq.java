package api.buyhood.domain.auth.dto.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupSellerReq {

	private String username;
	private String email;
	private String password;
	private String businessNumber;
	private String businessName;
	private String businessAddress;
}
