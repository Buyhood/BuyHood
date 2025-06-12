//package api.buyhood.domain.product.controller;
//
//import api.buyhood.domain.product.dto.request.PatchProductReq;
//import api.buyhood.domain.product.dto.request.RegisterProductReq;
//import api.buyhood.domain.product.dto.response.GetProductRes;
//import api.buyhood.domain.product.dto.response.PageProductRes;
//import api.buyhood.domain.product.dto.response.RegisterProductRes;
//import api.buyhood.domain.product.service.ProductService;
//import api.buyhood.dto.Response;
//import api.buyhood.security.AuthUser;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.web.PageableDefault;
//import org.springframework.security.access.annotation.Secured;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PatchMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api")
//public class ProductController {
//
//	private final ProductService productService;
//
//	@Secured("ROLE_SELLER")
//	@PostMapping("/v1/stores/{storeId}/products")
//	public Response<RegisterProductRes> registerProduct(
//		@AuthenticationPrincipal AuthUser currentUser,
//		@PathVariable Long storeId,
//		@Valid @RequestBody RegisterProductReq request
//	) {
//		RegisterProductRes response = productService.registerProduct(
//			currentUser.getId(),
//			storeId,
//			request.getProductName(),
//			request.getPrice(),
//			request.getStock(),
//			request.getCategoryIdList(),
//			request.getDescription()
//		);
//		return Response.ok(response);
//	}
//
//	@Secured("ROLE_SELLER")
//	@GetMapping("/v1/stores/{storeId}/products/{productId}")
//	public Response<GetProductRes> getProduct(@PathVariable Long storeId, @PathVariable Long productId) {
//		GetProductRes response = productService.getProduct(storeId, productId);
//		return Response.ok(response);
//	}
//
//	@Secured("ROLE_SELLER")
//	@GetMapping("/v1/stores/{storeId}/products")
//	public Response<Page<PageProductRes>> getAllProduct(
//		@PathVariable Long storeId,
//		@PageableDefault Pageable pageable
//	) {
//		Page<PageProductRes> response = productService.getAllProducts(storeId, pageable);
//		return Response.ok(response);
//	}
//
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
//
//	@Secured("ROLE_SELLER")
//	@PatchMapping("/v1/stores/{storeId}/products/{productId}")
//	public void patchProduct(
//		@AuthenticationPrincipal AuthUser currentUser,
//		@PathVariable Long storeId,
//		@PathVariable Long productId,
//		@Valid @RequestBody PatchProductReq request
//	) {
//		productService.patchProduct(
//			currentUser.getId(),
//			storeId,
//			productId,
//			request.getProductName(),
//			request.getPrice(),
//			request.getCategoryIdList(),
//			request.getDescription(),
//			request.getStock()
//		);
//	}
//
//	@Secured("ROLE_SELLER")
//	@DeleteMapping("/v1/stores/{storeId}/products/{productId}")
//	public void deleteProduct(
//		@AuthenticationPrincipal AuthUser currentUser,
//		@PathVariable Long storeId,
//		@PathVariable Long productId
//	) {
//		productService.deleteProduct(currentUser.getId(), storeId, productId);
//	}
//}
