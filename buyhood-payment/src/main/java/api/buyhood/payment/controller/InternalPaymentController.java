package api.buyhood.payment.controller;

import api.buyhood.dto.payment.PaymentFeignDto;
import api.buyhood.payment.service.InternalPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal")
public class InternalPaymentController {

    private final InternalPaymentService internalPaymentService;

    @GetMapping("/v1/payment")
    PaymentFeignDto findPaymentByOrderId(@RequestParam("order") Long orderId) {
        return internalPaymentService.findPaymentByOrderId(orderId);
    }

    @PatchMapping("/v1/payment/{paymentId}")
    void refundPayment(@PathVariable("paymentId") Long paymentId) {
        internalPaymentService.refundPayment(paymentId);
    }
}
