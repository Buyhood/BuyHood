package api.buyhood.domain.product.controller;

import api.buyhood.domain.product.dto.request.RegisteringProductReq;
import api.buyhood.domain.product.dto.response.RegisteringProductRes;
import api.buyhood.domain.product.service.ProductService;
import api.buyhood.global.common.dto.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
