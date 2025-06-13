package api.buyhood.order.client;

import api.buyhood.client.StoreClient;
import api.buyhood.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
        name = "StoreFeignClient",
        url = "${store.url}",
        configuration = FeignClientConfig.class
)
public interface StoreFeignClient extends StoreClient {
}
