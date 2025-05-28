package api.buyhood.domain.order.controller;

import api.buyhood.domain.order.dto.request.OrderReq;
import api.buyhood.domain.order.dto.response.CreateOrderRes;
import api.buyhood.domain.order.service.OrderService;
import api.buyhood.global.common.dto.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//todo: AuthUser 정보 추가
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;

    /**
     * 주문 생성
     * todo: AuthUser 추후 추가
     */

    @PostMapping("/v1/orders")
    public Response<CreateOrderRes> createOrder(
            @Valid @RequestBody OrderReq orderReq
    ) {
        return Response.ok(orderService.createOrder(orderReq));
    }

}
