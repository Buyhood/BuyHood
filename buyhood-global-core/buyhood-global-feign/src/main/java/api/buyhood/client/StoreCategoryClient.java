package api.buyhood.client;

import api.buyhood.dto.storecategory.StoreCategoryFeignDto;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface StoreCategoryClient {

	@GetMapping("/internal/v1/store-categories/{storeCategoryId}/exists")
	Boolean existsById(@PathVariable Long storeCategoryId);

	@GetMapping("/internal/v1/store-categories/{storeCategoryId}")
	StoreCategoryFeignDto getStoreCategoryResByIdOrElseThrow(@PathVariable Long storeCategoryId);

	@GetMapping("/internal/v1/store-categories/{storeCategoryName}")
	StoreCategoryFeignDto getStoreCategoryResByNameOrElseThrow(@PathVariable String storeCategoryName);

	@GetMapping("/internal/v1/store-categories")
	List<StoreCategoryFeignDto> getListStoreCategoryResByIds(List<Long> storeCategoryIds);

}