package api.buyhood.domain.product.controller;

import api.buyhood.domain.product.dto.request.CreateCategoryReq;
import api.buyhood.domain.product.dto.request.PatchCategoryReq;
import api.buyhood.domain.product.dto.response.CreateCategoryRes;
import api.buyhood.domain.product.dto.response.GetCategoryRes;
import api.buyhood.domain.product.dto.response.PageCategoryRes;
import api.buyhood.domain.product.service.CategoryService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CategoryController {

	private final CategoryService categoryService;

	@PostMapping("/v1/categories")
	public Response<CreateCategoryRes> createCategories(@Valid @RequestBody CreateCategoryReq request) {
		CreateCategoryRes response = categoryService.createCategory(request.getCategoryName(), request.getParentId());
		return Response.ok(response);
	}

	@GetMapping("/v1/categories/{categoryId}")
	public Response<GetCategoryRes> getCategory(@PathVariable Long categoryId) {
		GetCategoryRes response = categoryService.getCategory(categoryId);
		return Response.ok(response);
	}

	@GetMapping("/v1/categories")
	public Response<Page<PageCategoryRes>> getAllCategories(@PageableDefault Pageable pageable) {
		Page<PageCategoryRes> response = categoryService.getAllCategories(pageable);
		return Response.ok(response);
	}

	@GetMapping("/v1/categories/depth/{depth}")
	public Response<Page<PageCategoryRes>> getDepthCategories(
		@PathVariable int depth,
		@PageableDefault Pageable pageable
	) {
		Page<PageCategoryRes> response = categoryService.getDepthCategories(depth, pageable);
		return Response.ok(response);
	}

	@PatchMapping("/v1/categories/{categoryId}")
	public void patchCategory(
		@PathVariable Long categoryId,
		@Valid @RequestBody PatchCategoryReq request
	) {
		categoryService.patchCategory(categoryId, request.getNewCategoryName());
	}

	@DeleteMapping("/v1/categories/{categoryId}")
	public void deleteCategory(@PathVariable Long categoryId) {
		categoryService.deleteCategory(categoryId);
	}
	
}
