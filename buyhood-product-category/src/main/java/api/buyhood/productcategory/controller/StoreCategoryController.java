package api.buyhood.productcategory.controller;

import api.buyhood.productcategory.dto.request.store.CreateStoreCategoryReq;
import api.buyhood.productcategory.dto.request.store.PatchStoreCategoryReq;
import api.buyhood.productcategory.dto.response.store.CreateStoreCategoryRes;
import api.buyhood.productcategory.dto.response.store.GetStoreCategoryRes;
import api.buyhood.productcategory.dto.response.store.PageStoreCategoryRes;
import api.buyhood.productcategory.service.StoreCategoryService;
import api.dto.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.annotation.Secured;
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
public class StoreCategoryController {

	private final StoreCategoryService storeCategoryService;

	@Secured("ROLE_ADMIN")
	@PostMapping("/v1/store-categories")
	public Response<CreateStoreCategoryRes> createStoreCategory(@Valid @RequestBody CreateStoreCategoryReq request) {
		CreateStoreCategoryRes response = storeCategoryService.createStoreCategory(request.getName());
		return Response.ok(response);
	}

	@Secured("ROLE_ADMIN")
	@PatchMapping("/v1/store-categories/{storeCategoryId}")
	public void patchStoreCategory(
		@PathVariable Long storeCategoryId,
		@Valid @RequestBody PatchStoreCategoryReq request
	) {
		storeCategoryService.patchStoreCategoryName(storeCategoryId, request.getName());
	}

	@Secured("ROLE_ADMIN")
	@DeleteMapping("/v1/store-categories/{storeCategoryId}")
	public void deleteStoreCategory(@PathVariable Long storeCategoryId) {
		storeCategoryService.deleteStoreCategory(storeCategoryId);
	}

	@GetMapping("/v1/store-categories/{storeCategoryId}")
	public Response<GetStoreCategoryRes> getStoreCategory(@PathVariable Long storeCategoryId) {
		GetStoreCategoryRes response = storeCategoryService.getStoreCategory(storeCategoryId);
		return Response.ok(response);
	}

	@GetMapping("/v1/store-categories")
	public Response<Page<PageStoreCategoryRes>> getAllStoreCategories(@PageableDefault Pageable pageable) {
		Page<PageStoreCategoryRes> response = storeCategoryService.getAllStoreCategories(pageable);
		return Response.ok(response);
	}

	@GetMapping("/v1/store-categories/search")
	public Response<Page<PageStoreCategoryRes>> getStoreCategoriesByKeyword(
		@RequestParam(required = false) String keyword,
		@PageableDefault Pageable pageable
	) {
		Page<PageStoreCategoryRes> response = storeCategoryService.getStoreCategoriesByKeyword(keyword, pageable);
		return Response.ok(response);
	}
}
