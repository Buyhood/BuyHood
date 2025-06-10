package api.buyhood.product.client;

import api.buyhood.client.StoreClient;
import api.buyhood.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
	name = "storeFeignClient",
	url = "${store.url}",
	configuration = FeignClientConfig.class
)
public interface StoreFeignClient extends StoreClient {

}
