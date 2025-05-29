package api.buyhood.domain.order.controller;

import api.buyhood.domain.order.dto.request.CreateOrderReq;
import api.buyhood.domain.order.dto.response.CreateOrderRes;
import api.buyhood.domain.order.service.OrderService;
import api.buyhood.global.common.dto.Response;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
            @Valid @RequestBody CreateOrderReq createOrderReq
    ) {
        return Response.ok(orderService.createOrder(createOrderReq));
    }

}
