package api.buyhood.domain.order.controller;

import api.buyhood.domain.order.dto.request.AcceptOrderReq;
import api.buyhood.domain.order.dto.request.ApplyOrderReq;
import api.buyhood.domain.order.dto.request.RefundPaymentReq;
import api.buyhood.domain.order.dto.response.AcceptOrderRes;
import api.buyhood.domain.order.dto.response.ApplyOrderRes;
import api.buyhood.domain.order.dto.response.RejectOrderRes;
import api.buyhood.domain.order.service.OrderService;
import api.dto.Response;
import api.security.AuthUser;
import com.siot.IamportRestClient.exception.IamportResponseException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderController {

	private final OrderService orderService;

	//주문 요청
	@Secured("ROLE_USER")
	@PostMapping("/v1/orders/apply")
	public Response<ApplyOrderRes> applyOrder(
		@Valid @RequestBody ApplyOrderReq applyOrderReq,
		@AuthenticationPrincipal AuthUser authUser
	) {
		return Response.ok(orderService.applyOrder(applyOrderReq, authUser));
	}

	//주문 취소
	@Secured("ROLE_USER")
	@DeleteMapping("/v1/orders/{orderId}")
	public Response<String> deleteOrder(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable Long orderId,
		@Valid @RequestBody RefundPaymentReq refundPaymentReq
	) throws IamportResponseException, IOException {
		orderService.deleteOrder(authUser, orderId, refundPaymentReq);
		return Response.ok("주문 취소 성공");
	}


	//주문 승인
	@Secured("ROLE_SELLER")
	@PatchMapping("/v1/orders/{orderId}/accept")
	public Response<AcceptOrderRes> applyOrder(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable Long orderId,
		@RequestBody AcceptOrderReq req
	) {
		AcceptOrderRes res = orderService.acceptOrder(req, orderId, authUser);
		return Response.ok(res);
	}

	//주문 거절
	@Secured("ROLE_SELLER")
	@PatchMapping("/v1/orders/{orderId}/reject")
	public Response<RejectOrderRes> rejectOrder(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable Long orderId
	) {
		RejectOrderRes res = orderService.rejectOrder(orderId, authUser);
		return Response.ok(res);
	}

}
