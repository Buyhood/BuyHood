package api.buyhood.domain.order.controller;

import api.buyhood.domain.order.dto.response.OrderHistoryRes;
import api.buyhood.domain.order.service.OrderHistoryService;
import api.buyhood.global.common.dto.Response;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderHIsttoryController {

    private final OrderHistoryService orderHistoryService;

    /**
     * 주문 단건 조회
     */
    @GetMapping("/v1/orders/{orderId}")
    public Response<List<OrderHistoryRes>> getOrder(
            @PathVariable Long orderId
    ) {
        return Response.ok(orderHistoryService.findOrder(orderId));
    }

    /**
     * 주문 다건 조회 (인증된 사용자의 주문 전체 내역)
     * todo: AuthUser 추가
     */
    @GetMapping("/v1/orders/me")
    public Response<Page<OrderHistoryRes>> getOrdersByUser(
            @RequestParam(defaultValue = "0") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return Response.ok(orderHistoryService.getOrdersByUser(pageNum, pageSize));
    }

    /**
     * 주문 다건 조회 (Seller)
     */
    @GetMapping("/v1/orders/seller")
    public Response<Page<OrderHistoryRes>> getOrdersBySeller(
            @RequestParam(defaultValue = "0") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return Response.ok(orderHistoryService.getOrdersBySeller(pageNum, pageSize));
    }

    /**
     * 주문 다건 조회 (관리자용)
     */
    @GetMapping("/v1/orders")
    public Response<Page<OrderHistoryRes>> getOrders(
            @RequestParam(defaultValue = "0") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return Response.ok(orderHistoryService.getOrders(pageNum, pageSize));
    }
}
