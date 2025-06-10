package api.buyhood.domain.user.controller;

import api.buyhood.domain.user.service.InternalUserService;
import api.buyhood.dto.user.UserFeignDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class InternalUserController {

	private final InternalUserService internalUserService;

	@GetMapping("/v1/users/{id}")
	public UserFeignDto getUserInternal(@PathVariable Long id) {
		return internalUserService.getUserInternal(id);
	}

	@GetMapping("/v1/sellers/{id}")
	public UserFeignDto getSellerInternal(@PathVariable Long id) {
		return internalUserService.getSellerInternal(id);
	}

	@GetMapping("/v1/users/{id}/exists")
	public Boolean existsById(@PathVariable Long id) {
		return internalUserService.existsById(id);
	}
}
