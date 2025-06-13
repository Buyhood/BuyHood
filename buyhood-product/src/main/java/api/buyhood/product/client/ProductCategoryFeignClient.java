package api.buyhood.product.client;

import api.buyhood.client.ProductCategoryClient;
import api.buyhood.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
	name = "productCategoryFeignClient",
	url = "${product.category.url}",
	configuration = FeignClientConfig.class
)
public interface ProductCategoryFeignClient extends ProductCategoryClient {

}
