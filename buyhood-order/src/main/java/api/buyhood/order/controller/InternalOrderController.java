package api.buyhood.order.controller;

import api.buyhood.dto.order.OrderFeignDto;
import api.buyhood.order.service.InternalOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal")
public class InternalOrderController {
    private final InternalOrderService internalOrderService;

    @GetMapping("/v1/order/{orderId}")
    OrderFeignDto findOrder(@PathVariable("orderId") Long orderId) {
        return internalOrderService.findOrder(orderId);
    }

    @GetMapping("/v1/order/{orderId}/apply")
    OrderFeignDto findOrderForApplyPayment(@PathVariable("orderId") Long orderId) {
        return internalOrderService.findOrder(orderId);
    }
}
