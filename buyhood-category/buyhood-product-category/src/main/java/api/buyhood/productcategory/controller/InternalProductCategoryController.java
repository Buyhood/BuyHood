package api.buyhood.productcategory.controller;

import api.buyhood.dto.productcategory.ProductCategoryFeignDto;
import api.buyhood.productcategory.service.InternalProductCategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal")
public class InternalProductCategoryController {

	private final InternalProductCategoryService internalProductCategoryService;

	@GetMapping("/v1/product-categories/exists")
	public Boolean existsByIds(@RequestParam List<Long> categoryIds) {
		return internalProductCategoryService.existsByIds(categoryIds);
	}

	@GetMapping("/v1/product-categories/{id}/exists")
	public Boolean existsById(@PathVariable("id") Long categoryId) {
		return internalProductCategoryService.existsById(categoryId);
	}

	@GetMapping("/v1/product-categories/{id}")
	public ProductCategoryFeignDto getCategoryOrElseThrow(@PathVariable("id") Long categoryId) {
		return internalProductCategoryService.getCategoryOrElseThrow(categoryId);
	}

}
