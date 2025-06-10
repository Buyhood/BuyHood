package api.buyhood.client;

import api.buyhood.dto.productcategory.ProductCategoryFeignDto;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

public interface ProductCategoryClient {

	@GetMapping("/internal/v1/product-categories/exists")
	Boolean existsByIds(@RequestParam List<Long> categoryIds);

	@GetMapping("/internal/v1/product-categories/{id}/exists")
	Boolean existsById(@PathVariable Long id);

	@GetMapping("/internal/v1/product-categories/{id}")
	ProductCategoryFeignDto getCategoryOrElseThrow(@PathVariable Long id);
}
