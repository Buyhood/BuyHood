package api.buyhood.domain.user.controller;

import api.buyhood.domain.auth.entity.AuthUser;
import api.buyhood.domain.user.dto.req.ChangePasswordReq;
import api.buyhood.domain.user.dto.res.GetUserRes;
import api.buyhood.domain.user.service.UserService;
import api.buyhood.global.common.dto.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

	private final UserService userService;

	//단건 조회
	@GetMapping("/v1/users/{userId}")
	public Response<GetUserRes> getUser(@PathVariable Long userId) {
		GetUserRes response = userService.getUser(userId);

		return Response.ok(response);
	}

	//다건 조회
	@GetMapping("v1/users")
	public Response<Page<GetUserRes>> getUsers(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size) {
		Page<GetUserRes> response = userService.getAllUsers(page, size);

		return Response.ok(response);
	}

	//비밀번호 변경
	@PatchMapping("/v1/users")
	public Response<String> changePassword(
		@AuthenticationPrincipal AuthUser authUser,
		@RequestBody @Valid ChangePasswordReq req) {
		userService.changePassword(authUser, req);

		return Response.ok("비밀번호 변경에 성공했습니다.");
	}
}
