package api.buyhood.product.controller;

import api.buyhood.dto.Response;
import api.buyhood.product.dto.request.AddCategoryIdsReq;
import api.buyhood.product.dto.request.PatchProductReq;
import api.buyhood.product.dto.request.RegisterProductReq;
import api.buyhood.product.dto.request.RemoveCategoryIdsReq;
import api.buyhood.product.dto.response.GetProductRes;
import api.buyhood.product.dto.response.PageProductRes;
import api.buyhood.product.dto.response.RegisterProductRes;
import api.buyhood.product.service.ProductQueryService;
import api.buyhood.product.service.ProductService;
import api.buyhood.security.AuthUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProductController {

	private final ProductService productService;
	private final ProductQueryService productQueryService;

	@Secured("ROLE_SELLER")
	@PostMapping("/v1/stores/{storeId}/products")
	public Response<RegisterProductRes> registerProduct(
		@AuthenticationPrincipal AuthUser currentUser,
		@PathVariable Long storeId,
		@Valid @RequestBody RegisterProductReq request
	) {
		RegisterProductRes response = productService.registerProduct(
			currentUser.getId(),
			storeId,
			request.getProductName(),
			request.getPrice(),
			request.getStock(),
			request.getCategoryIdList(),
			request.getDescription()
		);
		return Response.ok(response);
	}

	@Secured("ROLE_SELLER")
	@GetMapping("/v1/stores/{storeId}/products/{productId}")
	public Response<GetProductRes> getProduct(
		@AuthenticationPrincipal AuthUser currentUser,
		@PathVariable Long storeId, @PathVariable Long productId) {
		GetProductRes response = productQueryService.getProduct(currentUser.getId(), storeId, productId);
		return Response.ok(response);
	}

	@Secured("ROLE_SELLER")
	@GetMapping("/v1/stores/{storeId}/products")
	public Response<Page<PageProductRes>> getAllProduct(
		@PathVariable Long storeId,
		@PageableDefault Pageable pageable
	) {
		Page<PageProductRes> response = productQueryService.getAllProducts(storeId, pageable);
		return Response.ok(response);
	}

//	@Secured("ROLE_SELLER")
//	@GetMapping("/v1/stores/{storeId}/products/keyword")
//	public Response<Page<PageProductRes>> getProductByKeyword(
//		@PathVariable Long storeId,
//		@RequestParam(required = false) String keyword,
//		@PageableDefault Pageable pageable
//	) {
//		Page<PageProductRes> response = productService.getProductByKeyword(storeId, keyword, pageable);
//		return Response.ok(response);
//	}

	@Secured("ROLE_SELLER")
	@PatchMapping("/v1/stores/{storeId}/products/{productId}")
	public Response<String> patchProduct(
		@AuthenticationPrincipal AuthUser currentUser,
		@PathVariable Long storeId,
		@PathVariable Long productId,
		@Valid @RequestBody PatchProductReq request
	) {
		productService.patchProduct(
			currentUser.getId(),
			storeId,
			productId,
			request.getProductName(),
			request.getPrice(),
			request.getStock(),
			request.getCategoryIds(),
			request.getDescription()
		);
		return Response.ok("상품정보 변경에 성공했습니다.");
	}

	//가게에 카테고리는 관리자만 가능
	@Secured("ROLE_ADMIN")
	@PostMapping("/v1/products/{productId}/categories")
	public Response<String> addCategoriesToProduct(
		@PathVariable Long productId,
		@RequestBody AddCategoryIdsReq req
	) {
		// 인증된 사용자인지 확인 후 서비스 호출
		productService.addCategoriesToProduct(productId, req.getCategoryIds());
		return Response.ok("상품에 카테고리가 성공적으로 추가되었습니다.");
	}

	//가게에 카테고리는 관리자만 가능
	@Secured("ROLE_ADMIN")
	@DeleteMapping("/v1/products/{productId}/categories")
	public Response<String> removeCategoriesFromProduct(
		@PathVariable Long productId,
		@RequestBody RemoveCategoryIdsReq req
	) {
		productService.removeCategoriesFromProduct(productId, req.getCategoryIds());
		return Response.ok("상품에 카테고리가 성공적으로 제거되었습니다.");
	}

	@Secured("ROLE_SELLER")
	@DeleteMapping("/v1/stores/{storeId}/products/{productId}")
	public Response<String> deleteProduct(
		@AuthenticationPrincipal AuthUser currentUser,
		@PathVariable Long storeId,
		@PathVariable Long productId
	) {
		productService.deleteProduct(currentUser.getId(), storeId, productId);
		return Response.ok("상품 삭제 완료");
	}
}
