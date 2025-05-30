package api.buyhood.domain.store.controller;

import api.buyhood.domain.store.dto.request.PatchStoreReq;
import api.buyhood.domain.store.dto.request.RegisteringStoreReq;
import api.buyhood.domain.store.dto.response.GetStoreRes;
import api.buyhood.domain.store.dto.response.PageStoreRes;
import api.buyhood.domain.store.dto.response.RegisteringStoreRes;
import api.buyhood.domain.store.service.StoreService;
import api.buyhood.global.common.dto.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

	@GetMapping("/v1/stores/{storeId}")
	public Response<GetStoreRes> getStore(@PathVariable Long storeId) {
		GetStoreRes response = storeService.getStore(storeId);
		return Response.ok(response);
	}

	@GetMapping("/v1/stores")
	public Response<Page<PageStoreRes>> getAllStore(@PageableDefault Pageable pageable) {
		Page<PageStoreRes> response = storeService.getAllStore(pageable);
		return Response.ok(response);
	}

	@GetMapping("/v1/stores/keyword")
	public Response<Page<PageStoreRes>> getStoreByKeyword(
		@RequestParam(required = false) String keyword,
		@PageableDefault Pageable pageable
	) {
		Page<PageStoreRes> response = storeService.getStoreByKeyword(keyword, pageable);
		return Response.ok(response);
	}

	@PatchMapping("/v1/stores/{storeId}")
	public void patchStore(@PathVariable Long storeId, @RequestBody PatchStoreReq request) {
		storeService.patchStore(
			storeId,
			request.getStoreName(),
			request.getAddress(),
			request.getSellerId(),
			request.getDescription(),
			request.getOpenedAt(),
			request.getClosedAt()
		);
	}

	@DeleteMapping("/v1/stores/{storeId}")
	public void deleteStore(@PathVariable Long storeId) {
		storeService.deleteStore(storeId);
	}

}
