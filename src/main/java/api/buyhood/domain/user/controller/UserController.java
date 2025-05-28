package api.buyhood.domain.user.controller;

import api.buyhood.domain.user.dto.res.GetUserRes;
import api.buyhood.domain.user.service.UserService;
import api.buyhood.global.common.dto.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

	private final UserService userService;

	@GetMapping("/v1/users/{userId}")
	public Response<GetUserRes> getUser(@PathVariable @Valid Long userId) {
		GetUserRes getUserRes = userService.getUser(userId);

		return Response.ok(getUserRes);
	}

	@GetMapping("v1/users")
	public Response<Page<GetUserRes>> getUsers(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size) {
		Page<GetUserRes> getUserRes = userService.getAllUsers(page, size);
		
		return Response.ok(getUserRes);
	}
}
