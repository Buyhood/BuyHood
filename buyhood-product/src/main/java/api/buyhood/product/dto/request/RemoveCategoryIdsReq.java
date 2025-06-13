package api.buyhood.product.dto.request;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RemoveCategoryIdsReq {

	private final List<Long> categoryIds;
}
