package api.buyhood.productcategory.controller;

import api.buyhood.dto.Response;
import api.buyhood.productcategory.dto.request.CreateProductCategoryReq;
import api.buyhood.productcategory.dto.request.PatchProductCategoryReq;
import api.buyhood.productcategory.dto.response.CreateProductCategoryRes;
import api.buyhood.productcategory.dto.response.GetProductCategoryRes;
import api.buyhood.productcategory.dto.response.PageProductCategoryRes;
import api.buyhood.productcategory.service.ProductCategoryService;
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
public class ProductCategoryController {

	private final ProductCategoryService productCategoryService;

	@Secured("ROLE_SELLER")
	@PostMapping("/v1/product-categories")
	public Response<CreateProductCategoryRes> createStoreCategory(
		@Valid @RequestBody CreateProductCategoryReq request
	) {
		CreateProductCategoryRes response =
			productCategoryService.createProductCategory(request.getName(), request.getParentId());
		return Response.ok(response);
	}

	@Secured("ROLE_SELLER")
	@PatchMapping("/v1/product-categories/{productCategoryId}")
	public void patchStoreCategory(
		@PathVariable Long productCategoryId,
		@Valid @RequestBody PatchProductCategoryReq request
	) {
		productCategoryService.patchProductCategoryName(productCategoryId, request.getName());
	}

	@Secured("ROLE_SELLER")
	@DeleteMapping("/v1/product-categories/{productCategoryId}")
	public void deleteProductCategory(@PathVariable Long productCategoryId) {
		productCategoryService.deleteProductCategory(productCategoryId);
	}

	@GetMapping("/v1/product-categories/{productCategoryId}")
	public Response<GetProductCategoryRes> getProductCategory(@PathVariable Long productCategoryId) {
		GetProductCategoryRes response = productCategoryService.getProductCategory(productCategoryId);
		return Response.ok(response);
	}

	@GetMapping("/v1/product-categories")
	public Response<Page<PageProductCategoryRes>> getAllProductCategories(@PageableDefault Pageable pageable) {
		Page<PageProductCategoryRes> response = productCategoryService.getAllProductCategories(pageable);
		return Response.ok(response);
	}

	@GetMapping("/v1/product-categories/search")
	public Response<Page<PageProductCategoryRes>> getProductCategoriesByKeyword(
		@RequestParam(required = false) String keyword,
		@PageableDefault Pageable pageable
	) {
		Page<PageProductCategoryRes> response = productCategoryService.getProductCategoriesByKeyword(keyword, pageable);
		return Response.ok(response);
	}
}
