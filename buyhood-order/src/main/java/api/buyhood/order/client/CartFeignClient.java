package api.buyhood.order.client;

import api.buyhood.client.CartClient;
import api.buyhood.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
        name = "CartFeignClient",
        url = "${cart.url}",
        configuration = FeignClientConfig.class
)
public interface CartFeignClient extends CartClient {
}
