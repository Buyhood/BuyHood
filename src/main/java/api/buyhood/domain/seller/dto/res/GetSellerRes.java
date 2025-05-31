package api.buyhood.domain.seller.dto.res;

import api.buyhood.domain.seller.entity.Seller;
import api.buyhood.domain.user.enums.UserRole;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GetSellerRes {

	private final String username;
	private final String email;
	private final String businessNumber;
	private final UserRole role;
	private final String phoneNumber;

	public static GetSellerRes of(Seller seller) {
		return new GetSellerRes(
			seller.getUsername(),
			seller.getEmail(),
			seller.getBusinessNumber(),
			seller.getRole(),
			seller.getPhoneNumber()
		);
	}
}
