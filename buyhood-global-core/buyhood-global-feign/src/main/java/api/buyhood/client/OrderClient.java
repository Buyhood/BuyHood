package api.buyhood.client;

import api.buyhood.dto.order.OrderFeignDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface OrderClient {

    @GetMapping("/internal/v1/order/{orderId}")
    OrderFeignDto findOrder(@PathVariable("orderId") Long orderId);

    // 사용자 브라우저에서 결제용 화면에 보여주기 위한 호출
    @GetMapping("/internal/v1/order/{orderId}/apply")
    OrderFeignDto findOrderForApplyPayment(@PathVariable("orderId") Long orderId);
}
