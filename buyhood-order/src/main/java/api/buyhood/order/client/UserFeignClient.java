package api.buyhood.order.client;

import api.buyhood.client.UserClient;
import api.buyhood.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
        name = "UserFeignClient",
        url = "${user.url}",
        configuration = FeignClientConfig.class
)
public interface UserFeignClient extends UserClient {
}
