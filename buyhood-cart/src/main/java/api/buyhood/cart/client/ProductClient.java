package api.buyhood.cart.client;

import api.buyhood.cart.dto.res.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", url = "http://localhost:8083")
public interface ProductClient {

    @GetMapping("/api/v1/products/{productId}")
	ProductResponse getProduct(@PathVariable Long productId);
}