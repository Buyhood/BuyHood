package api.buyhood.domain.product.controller;

import api.buyhood.domain.product.dto.request.RegisteringProductReq;
import api.buyhood.domain.product.dto.response.GetProductRes;
import api.buyhood.domain.product.dto.response.PageProductRes;
import api.buyhood.domain.product.dto.response.RegisteringProductRes;
import api.buyhood.domain.product.service.ProductService;
import api.buyhood.global.common.dto.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
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
	public Response<RegisteringProductRes> registeringProduct(@Valid @RequestBody RegisteringProductReq request) {
		RegisteringProductRes response = productService.registerProduct(
			request.getProductName(),
			request.getPrice(),
			request.getStock(),
			request.getCategoryId(),
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
}
