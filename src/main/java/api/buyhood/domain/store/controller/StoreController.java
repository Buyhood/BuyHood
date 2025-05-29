package api.buyhood.domain.store.controller;

import api.buyhood.domain.store.dto.request.RegisteringStoreReq;
import api.buyhood.domain.store.dto.response.RegisteringStoreRes;
import api.buyhood.domain.store.service.StoreService;
import api.buyhood.global.common.dto.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class StoreController {

	private final StoreService storeService;

	@PostMapping("/v1/stores")
	public Response<RegisteringStoreRes> registerStore(@Valid @RequestBody RegisteringStoreReq request) {
		RegisteringStoreRes response = storeService.registerStore(
			request.getStoreName(),
			request.getAddress(),
			request.getSellerId(),
			request.getDescription(),
			request.getOpenedAt(),
			request.getClosedAt()
		);
		return Response.ok(response);
	}
}
