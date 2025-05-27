package api.buyhood.domain.cart.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CreateCartReq {
    private List<CartReq> cartItems;
}
