package api.buyhood.productcategory.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreateProductCategoryReq {

	@NotBlank(message = "상품 카테고리 이름은 공백일 수 없습니다.")
	@Size(min = 1, max = 12, message = "상품 카테고리는 최소 1글자, 최대 12글자여야 합니다.")
	private final String name;

	private final Long parentId;
}
