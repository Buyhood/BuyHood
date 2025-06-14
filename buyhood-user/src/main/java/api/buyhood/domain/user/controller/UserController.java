package api.buyhood.domain.user.controller;

import api.buyhood.domain.user.dto.req.ChangeSellerRoleReq;
import api.buyhood.domain.user.dto.req.ChangeUserPasswordReq;
import api.buyhood.domain.user.dto.req.DeleteUserReq;
import api.buyhood.domain.user.dto.req.PatchUserReq;
import api.buyhood.domain.user.dto.res.GetUserRes;
import api.buyhood.domain.user.dto.res.PatchUserRes;
import api.buyhood.domain.user.service.UserService;
import api.buyhood.dto.Response;
import api.buyhood.security.AuthUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
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
	@PatchMapping("/v1/users/password")
	public Response<String> changePassword(
		@AuthenticationPrincipal AuthUser authUser,
		@RequestBody @Valid ChangeUserPasswordReq req) {
		userService.changePassword(authUser, req);

		return Response.ok("비밀번호 변경에 성공했습니다.");
	}

	//유저 정보 변경(로그인한 유저 전용)
	@PatchMapping("/v1/users")
	public Response<PatchUserRes> patchUser(
		@AuthenticationPrincipal AuthUser authUser,
		@RequestBody @Valid PatchUserReq req
	) {
		PatchUserRes patchUserRes = userService.patchUser(authUser, req);
		return Response.ok(patchUserRes);
	}

	//유저 삭제
	@DeleteMapping("/v1/users")
	public Response<String> deleteUser(
		@AuthenticationPrincipal AuthUser authUser,
		@RequestBody @Valid DeleteUserReq req) {
		userService.deleteUser(authUser, req);

		return Response.ok("회원탈퇴에 성공했습니다.");
	}

	@PatchMapping("/v1/users/{userId}/grant-seller")
	public Response<String> grantSeller(@PathVariable Long userId, ChangeSellerRoleReq req) {
		userService.grantSeller(userId, req);
		return Response.ok("SELLER 권한이 부여되었습니다. 재로그인 해주세요");
	}

	@PatchMapping("/v1/users/{userId}/grant-admin")
	public Response<String> grantAdmin(@PathVariable Long userId) {
		userService.grantAdmin(userId);
		return Response.ok("ADMIN 권한이 부여되었습니다. 재로그인 해주세요");
	}

}
