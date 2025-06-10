package api.buyhood.dto.productcategory;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProductCategoryFeignDto {

	private Long categoryId;
	private String categoryName;
	private int depth;
	private Long parentId;
	private List<Long> childrenIds;
	
}
