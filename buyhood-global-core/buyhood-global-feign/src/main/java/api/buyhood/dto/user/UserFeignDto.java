package api.buyhood.dto.user;

import api.buyhood.enums.UserRole;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserFeignDto {

	private Long userId;
	private String username;
	private String email;
	private UserRole role;
	private String address;

}

