package api.buyhood.dto.product.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class GetProductReq {
    private final List<Long> productIdList;
}
