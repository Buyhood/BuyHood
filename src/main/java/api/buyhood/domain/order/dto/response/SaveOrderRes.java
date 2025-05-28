package api.buyhood.domain.order.dto.response;

import api.buyhood.domain.product.entity.Product;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class SaveOrderRes {
    private Map<Long, Product> productMap;

    @Builder
    public SaveOrderRes (Map<Long, Product> productMap) {
        this.productMap = productMap;
    }

    public static SaveOrderRes of (Map<Long, Product> productMap) {
        return SaveOrderRes.builder()
                .productMap(productMap)
                .build();
    }
}
