package api.buyhood.dto.product.request;

import api.buyhood.dto.product.response.ProductFeignDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class DecreaseStockProductReq {
    private final List<Long> productIdList;

    private final List<Integer> quantityList;

    private final Map<Long, ProductFeignDto> productMap;
}
