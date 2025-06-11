package api.buyhood.store.client;

import api.buyhood.client.StoreCategoryClient;
import api.buyhood.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
	name = "storeCategoryFeignClient",
	url = "${store.category.url}",
	configuration = {FeignClientConfig.class}
)
public interface StoreCategoryFeignClient extends StoreCategoryClient {

}
