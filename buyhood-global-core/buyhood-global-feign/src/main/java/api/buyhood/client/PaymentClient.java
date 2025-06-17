package api.buyhood.client;

import api.buyhood.dto.payment.PaymentFeignDto;
import org.springframework.web.bind.annotation.*;

public interface PaymentClient {
    @GetMapping("/internal/v1/payment")
    PaymentFeignDto findPayment(@RequestParam("order") Long orderId);

    @PutMapping("/internal/v1/payment/{paymentId}")
    void refundPayment(@PathVariable("paymentId") Long paymentId);
}
