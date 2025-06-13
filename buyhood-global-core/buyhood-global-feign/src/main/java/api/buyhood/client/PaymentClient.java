package api.buyhood.client;

import api.buyhood.dto.payment.PaymentFeignDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

public interface PaymentClient {
    @GetMapping("/internal/v1/payment")
    PaymentFeignDto findPayment(@RequestParam("order") Long orderId);

    @PatchMapping("/internal/v1/payment/{paymentId}")
    void refundPayment(@PathVariable("paymentId") Long paymentId);
}
