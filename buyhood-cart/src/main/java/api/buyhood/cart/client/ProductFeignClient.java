package api.buyhood.cart.client;

import api.buyhood.client.ProductClient;
import api.buyhood.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
        name = "ProductFeignClient",
        url = "${product.url}",
        configuration = FeignClientConfig.class
)
public interface ProductFeignClient extends ProductClient {
}
