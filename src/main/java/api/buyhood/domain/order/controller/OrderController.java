package api.buyhood.domain.order.controller;

import api.buyhood.domain.auth.entity.AuthUser;
import api.buyhood.domain.order.dto.request.CreateOrderReq;
import api.buyhood.domain.order.dto.response.CreateOrderRes;
import api.buyhood.domain.order.dto.request.OrderReq;
import api.buyhood.domain.order.dto.response.OrderRes;
import api.buyhood.domain.order.dto.response.CreateOrderRes;
import api.buyhood.domain.order.service.OrderService;
import api.buyhood.global.common.dto.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderController {

	private final OrderService orderService;

	@PostMapping("/v1/orders")
	public Response<CreateOrderRes> createOrder(
		@Valid @RequestBody CreateOrderReq createOrderReq,
		@AuthenticationPrincipal AuthUser authUser
	) {
		return Response.ok(orderService.createOrder(createOrderReq, authUser));
	}

    /**
     * 주문 단건 조회
     */
    @GetMapping("/v1/orders/{orderId}")
    public Response<OrderRes> getOrder(
            @PathVariable Long orderId
    ) {
        return Response.ok(orderService.findOrder(orderId));
    }

    /**
     * 주문 다건 조회
     */
    @GetMapping("/v1/orders")
    public Response<Page<OrderRes>> getOrders(
            @RequestParam(defaultValue = "0") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return Response.ok(orderService.getOrders(pageNum, pageSize));
    }

    /**
     * 주문 취소
     * todo: AuthUser 추후 추가
     */
    @DeleteMapping("/v1/orders/{orderId}")
    public Response<String> deleteOrder(
            @PathVariable Long orderId
    ) {
        orderService.deleteOrder(orderId);
        return Response.ok("주문 취소 성공");
    }
}
