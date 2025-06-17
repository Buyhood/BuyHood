package api.buyhood.dto.product.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public class RollbackStockProductReq {
    private final Map<Long, Integer> orderHistoryMap;
}
