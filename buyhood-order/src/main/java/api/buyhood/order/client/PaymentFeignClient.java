package api.buyhood.order.client;

import api.buyhood.client.PaymentClient;
import api.buyhood.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
        name = "PaymentFeignClient",
        url = "${payment.url}",
        configuration = FeignClientConfig.class
)
public interface PaymentFeignClient extends PaymentClient {
}
