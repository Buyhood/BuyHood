package api.buyhood.domain.seller.controller;

import api.buyhood.domain.seller.dto.req.ChangeSellerPasswordReq;
import api.buyhood.domain.seller.dto.req.DeleteSellerReq;
import api.buyhood.domain.seller.dto.res.GetSellerRes;
import api.buyhood.domain.seller.service.SellerService;
import api.dto.Response;
import api.security.AuthUser;
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
public class SellerController {

	private final SellerService sellerService;

	//단건 조회
	@GetMapping("/v1/sellers/{sellerId}")
	public Response<GetSellerRes> getSeller(@PathVariable Long sellerId) {
		GetSellerRes res = sellerService.getUser(sellerId);

		return Response.ok(res);
	}

	//다건 조회
	@GetMapping("/v1/sellers")
	public Response<Page<GetSellerRes>> getSellers(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size) {
		Page<GetSellerRes> res = sellerService.getAllSellers(page, size);

		return Response.ok(res);
	}

	//셀러 탈퇴
	@DeleteMapping("/v1/sellers")
	public Response<String> deleteSeller(
		@AuthenticationPrincipal AuthUser authUser,
		@RequestBody @Valid DeleteSellerReq req) {
		sellerService.deleteSeller(authUser, req);

		return Response.ok("회원탈퇴에 성공했습니다.");
	}

	//비밀번호 변경
	@PatchMapping("/v1/sellers/password")
	public Response<String> changePassword(
		@AuthenticationPrincipal AuthUser authUser,
		@RequestBody @Valid ChangeSellerPasswordReq req) {
		sellerService.changePassword(authUser, req);

		return Response.ok("비밀번호 변경에 성공했습니다.");
	}
}
