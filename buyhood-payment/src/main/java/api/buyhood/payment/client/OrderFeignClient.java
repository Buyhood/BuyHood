package api.buyhood.payment.client;

import api.buyhood.client.OrderClient;
import api.buyhood.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
        name = "OrderFeignClient",
        url = "${order.url}",
        configuration = FeignClientConfig.class
)
public interface OrderFeignClient extends OrderClient {
}
