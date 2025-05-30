package api.buyhood.domain.order.controller;

import api.buyhood.domain.auth.entity.AuthUser;
import api.buyhood.domain.order.dto.request.CreateOrderReq;
import api.buyhood.domain.order.dto.response.CreateOrderRes;
import api.buyhood.domain.order.service.OrderService;
import api.buyhood.global.common.dto.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	 * 주문 취소
	 */
	@DeleteMapping("/v1/orders/{orderId}")
	public Response<String> deleteOrder(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable Long orderId
	) {
		orderService.deleteOrder(authUser, orderId);
		return Response.ok("주문 취소 성공");
	}
}
