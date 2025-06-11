package api.buyhood.store.controller;

import api.buyhood.dto.store.StoreFeignDto;
import api.buyhood.store.service.InternalStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal")
public class InternalStoreController {

	private final InternalStoreService internalStoreService;

	@GetMapping("/v1/stores/{id}")
	public StoreFeignDto getStoreOrElseThrow(@PathVariable("id") Long storeId) {
		return internalStoreService.getStoreOrElseThrow(storeId);
	}

	@GetMapping("/v1/stores/{id}/exists")
	public Boolean existsById(@PathVariable("id") Long storeId) {
		return internalStoreService.existsById(storeId);
	}
}
