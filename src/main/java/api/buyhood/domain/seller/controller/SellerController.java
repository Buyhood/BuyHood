package api.buyhood.domain.seller.controller;

import api.buyhood.domain.seller.dto.res.GetSellerRes;
import api.buyhood.domain.seller.service.SellerService;
import api.buyhood.global.common.dto.Response;
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
}
