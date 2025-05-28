package api.buyhood.domain.cart.controller;

import api.buyhood.domain.cart.dto.request.CreateCartReq;
import api.buyhood.domain.cart.dto.response.CartRes;
import api.buyhood.domain.cart.service.CartService;
import api.buyhood.global.common.dto.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CartController {

    private final CartService cartService;

    /**
     * 장바구니 담기 기능
     * todo: 권한 AuthUser 정보 추가
     */
    @PostMapping("/v1/carts")
    public Response<CartRes> addItemsToCart(
            @Valid @RequestBody CreateCartReq createCartReq
    ) {
        return Response.ok(cartService.addItemsToCart(createCartReq));
    }
}
