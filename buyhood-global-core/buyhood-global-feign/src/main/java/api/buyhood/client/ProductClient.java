package api.buyhood.client;

import api.buyhood.dto.product.request.GetProductReq;
import api.buyhood.dto.product.response.ProductFeignDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface ProductClient {

	@GetMapping("/internal/v1/products/{id}")
	ProductFeignDto getProductOrElseThrow(@PathVariable Long id);

	@PostMapping("/internal/v1/products")
	List<ProductFeignDto> getProductsOrElseThrow(@RequestBody GetProductReq getProductReq);

	@GetMapping("/internal/v1/products/{id}/exists")
	Boolean existsById(@PathVariable Long id);

}
