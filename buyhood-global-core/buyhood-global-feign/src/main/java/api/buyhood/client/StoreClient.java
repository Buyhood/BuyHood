package api.buyhood.client;

import api.buyhood.dto.store.StoreFeignDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface StoreClient {

	@GetMapping("/internal/v1/stores/{id}")
	StoreFeignDto getStoreOrElseThrow(@PathVariable Long id);

	@GetMapping("/internal/v1/stores/{id}/exists")
	Boolean existsById(@PathVariable Long id);
}
