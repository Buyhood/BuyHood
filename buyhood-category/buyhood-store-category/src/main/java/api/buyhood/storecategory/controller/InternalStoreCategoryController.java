package api.buyhood.storecategory.controller;

import api.buyhood.dto.storecategory.StoreCategoryFeignDto;
import api.buyhood.storecategory.service.InternalStoreCategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal")
public class InternalStoreCategoryController {

	private final InternalStoreCategoryService internalStoreCategoryService;

	@GetMapping("/v1/store-categories/{storeCategoryId}/exists")
	public Boolean existsById(@PathVariable Long storeCategoryId) {
		return internalStoreCategoryService.existsById(storeCategoryId);
	}

	@GetMapping("/v1/store-categories/{storeCategoryId}")
	public StoreCategoryFeignDto getStoreCategoryResByIdOrElseThrow(@PathVariable Long storeCategoryId) {
		return internalStoreCategoryService.getStoreCategoryResByIdOrElseThrow(storeCategoryId);
	}

	@GetMapping("/v1/store-categories/{storeCategoryName}")
	public StoreCategoryFeignDto getStoreCategoryResByNameOrElseThrow(@PathVariable String storeCategoryName) {
		return internalStoreCategoryService.getStoreCategoryResByNameOrElseThrow(storeCategoryName);
	}

	@GetMapping("/v1/store-categories")
	public List<StoreCategoryFeignDto> getListStoreCategoryResByIds(List<Long> storeCategoryIds) {
		return internalStoreCategoryService.getListStoreCategoryResByIds(storeCategoryIds);
	}

}
