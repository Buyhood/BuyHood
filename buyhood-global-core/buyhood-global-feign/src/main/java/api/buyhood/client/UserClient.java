package api.buyhood.client;

import api.buyhood.dto.user.UserFeignDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface UserClient {

	@GetMapping("/internal/v1/users/{userId}/exists")
	Boolean existsById(@PathVariable Long userId);

	@GetMapping("/internal/v1/sellers/{sellerId}")
	UserFeignDto getRoleSellerOrElseThrow(@PathVariable Long sellerId);

	@GetMapping("/internal/v1/users/{userId}")
	UserFeignDto getRoleUserOrElseThrow(@PathVariable Long userId);
	
}
