package api.buyhood.domain.order.controller;

import api.buyhood.domain.order.dto.response.OrderHistoryRes;
import api.buyhood.domain.auth.entity.AuthUser;
import api.buyhood.domain.order.dto.response.GetOrderRes;
import api.buyhood.domain.order.service.OrderHistoryService;
import api.dto.Response;
import api.security.AuthUser;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderHistoryController {

	private final OrderHistoryService orderHistoryService;

	/**
	 * 주문 단건 조회
	 */
	@GetMapping("/v1/orders/{orderId}")
	public Response<List<GetOrderRes>> getOrder(
		@PathVariable Long orderId
	) {
		return Response.ok(orderHistoryService.findOrder(orderId));
	}

	/**
	 * 주문 다건 조회 (인증된 사용자의 주문 전체 내역)
	 */
	@Secured("ROLE_USER")
	@GetMapping("/v1/orders/me/all")
	public Response<Page<GetOrderRes>> getOrdersByUser(
		@RequestParam(defaultValue = "0") int pageNum,
		@RequestParam(defaultValue = "10") int pageSize,
		@AuthenticationPrincipal AuthUser authUser
	) {
		return Response.ok(orderHistoryService.getOrdersByUser(pageNum, pageSize, authUser.getId()));
	}

	/**
	 * 주문 다건 조회 (Seller)
	 */
	@Secured("ROLE_SELLER")
	@GetMapping("/v1/orders/{storeId}/all")
	public Response<Page<GetOrderRes>> getOrdersBySeller(
		@AuthenticationPrincipal AuthUser authUser,
		@RequestParam(defaultValue = "0") int pageNum,
		@RequestParam(defaultValue = "10") int pageSize,
		@PathVariable @Valid Long storeId
	) {
		return Response.ok(orderHistoryService.getOrdersBySeller(pageNum, pageSize, storeId, authUser.getId()));
	}

	/**
	 * 주문 다건 조회 (관리자용)
	 */
	@GetMapping("/v1/orders/all")
	public Response<Page<GetOrderRes>> getOrders(
		@RequestParam(defaultValue = "0") int pageNum,
		@RequestParam(defaultValue = "10") int pageSize
	) {
		return Response.ok(orderHistoryService.getOrders(pageNum, pageSize));
	}
}
