package api.buyhood.domain.product.controller;

import api.buyhood.domain.product.dto.request.PatchProductReq;
import api.buyhood.domain.product.dto.request.RegisterProductReq;
import api.buyhood.domain.product.dto.response.GetProductRes;
import api.buyhood.domain.product.dto.response.PageProductRes;
import api.buyhood.domain.product.dto.response.RegisterProductRes;
import api.buyhood.domain.product.service.ProductService;
import api.buyhood.global.common.dto.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProductController {

	private final ProductService productService;

	@PostMapping("/v1/products")
	public Response<RegisterProductRes> registeringProduct(@Valid @RequestBody RegisterProductReq request) {
		RegisterProductRes response = productService.registerProduct(
			request.getProductName(),
			request.getPrice(),
			request.getStock(),
			request.getCategoryIdList(),
			request.getDescription()
		);
		return Response.ok(response);
	}

	@GetMapping("/v1/products/{productId}")
	public Response<GetProductRes> getProduct(@PathVariable Long productId) {
		GetProductRes response = productService.getProduct(productId);
		return Response.ok(response);
	}

	@GetMapping("/v1/products")
	public Response<Page<PageProductRes>> getAllProduct(@PageableDefault Pageable pageable) {
		Page<PageProductRes> response = productService.getAllProducts(pageable);
		return Response.ok(response);
	}

	@GetMapping("/v1/products/keyword")
	public Response<Page<PageProductRes>> getProductByKeyword(
		@RequestParam(required = false) String keyword,
		@PageableDefault Pageable pageable
	) {
		Page<PageProductRes> response = productService.getProductByKeyword(keyword, pageable);
		return Response.ok(response);
	}

	@PatchMapping("/v1/products/{productId}")
	public void patchProduct(@PathVariable Long productId, @Valid @RequestBody PatchProductReq request) {
		productService.patchProduct(
			productId,
			request.getProductName(),
			request.getPrice(),
			request.getCategoryIdList(),
			request.getDescription(),
			request.getStock()
		);
	}

	@DeleteMapping("/v1/products/{productId}")
	public void deleteProduct(@PathVariable Long productId) {
		productService.deleteProduct(productId);
	}
}
