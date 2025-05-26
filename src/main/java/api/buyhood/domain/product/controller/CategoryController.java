package api.buyhood.domain.product.controller;

import api.buyhood.domain.product.dto.request.CreateCategoryReq;
import api.buyhood.domain.product.dto.response.CreateCategoryRes;
import api.buyhood.domain.product.service.CategoryService;
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
public class CategoryController {

	private final CategoryService productService;

	@PostMapping("/v1/categories")
	public Response<CreateCategoryRes> createCategories(@Valid @RequestBody CreateCategoryReq request) {
		CreateCategoryRes response = productService.createCategory(request.getCategoryName(), request.getParentId());
		return Response.ok(response);
	}

}
