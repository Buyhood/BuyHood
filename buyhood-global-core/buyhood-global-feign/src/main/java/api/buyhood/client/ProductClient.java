package api.buyhood.client;

import api.buyhood.dto.product.ProductFeignDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface ProductClient {

	@GetMapping("/internal/v1/products/{id}")
	ProductFeignDto getProductOrElseThrow(@PathVariable Long id);

	@GetMapping("/internal/v1/products/{id}/exists")
	Boolean existsById(@PathVariable Long id);

}
