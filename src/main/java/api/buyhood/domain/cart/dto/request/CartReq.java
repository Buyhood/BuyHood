package api.buyhood.domain.cart.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CartReq {
    private Long productId;
    private int quantity;
}
