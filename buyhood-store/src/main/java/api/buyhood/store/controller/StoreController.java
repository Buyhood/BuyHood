package api.buyhood.store.controller;

import api.buyhood.dto.Response;
import api.buyhood.security.AuthUser;
import api.buyhood.store.dto.request.PatchStoreReq;
import api.buyhood.store.dto.request.RegisterStoreReq;
import api.buyhood.store.dto.response.GetStoreRes;
import api.buyhood.store.dto.response.PageStoreRes;
import api.buyhood.store.dto.response.RegisterStoreRes;
import api.buyhood.store.service.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

	@Secured("ROLE_SELLER")
	@PostMapping("/v1/stores")
	public Response<RegisterStoreRes> registerStore(
		@AuthenticationPrincipal AuthUser currentUser,
		@Valid @RequestBody RegisterStoreReq request
	) {
		RegisterStoreRes response = storeService.registerStore(
			currentUser.getId(),
			request.getStoreName(),
			request.getAddress(),
			request.getCategoryId(),
			request.getIsDeliverable(),
			request.getDescription(),
			request.getOpenedAt(),
			request.getClosedAt()
		);
		return Response.ok(response);
	}

	@Secured("ROLE_SELLER")
	@PatchMapping("/v1/stores/{storeId}")
	public void patchStore(
		@AuthenticationPrincipal AuthUser currentUser,
		@PathVariable Long storeId,
		@RequestBody PatchStoreReq request
	) {
		storeService.patchStore(
			currentUser.getId(),
			storeId,
			request.getStoreName(),
			request.getAddress(),
			request.getIsDeliverable(),
			request.getDescription(),
			request.getOpenedAt(),
			request.getClosedAt()
		);
	}

	@Secured("ROLE_SELLER")
	@DeleteMapping("/v1/stores/{storeId}")
	public void deleteStore(@AuthenticationPrincipal AuthUser currentUser, @PathVariable Long storeId) {
		storeService.inActiveStore(currentUser.getId(), storeId);
	}

	@GetMapping("/v1/stores/{storeId}")
	public Response<GetStoreRes> getActiveStore(@PathVariable Long storeId) {
		GetStoreRes response = storeService.getActiveStore(storeId);
		return Response.ok(response);
	}

	@GetMapping("/v1/stores")
	public Response<Page<PageStoreRes>> getActiveStoresByCategoryName(
		@RequestParam(required = false) String storeCategoryName,
		@PageableDefault Pageable pageable
	) {
		Page<PageStoreRes> response;

		if (storeCategoryName == null) {
			response = storeService.getActiveStores(pageable);
		} else {
			response = storeService.getActiveStoresByCategoryName(storeCategoryName, pageable);
		}

		return Response.ok(response);
	}

}
