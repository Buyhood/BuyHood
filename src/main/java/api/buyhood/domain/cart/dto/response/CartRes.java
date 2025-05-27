package api.buyhood.domain.cart.dto.response;

import api.buyhood.domain.cart.dto.request.CreateCartReq;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CartRes {
    private final CreateCartReq cartList;

    @Builder
    private CartRes(CreateCartReq cartList) {
        this.cartList = cartList;
    }
}
