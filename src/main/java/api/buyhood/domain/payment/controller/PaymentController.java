package api.buyhood.domain.payment.controller;

import api.buyhood.domain.payment.dto.request.ApplyPaymentReq;
import api.buyhood.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class PaymentController {

    private final PaymentService paymentService;

    /* 결제 요청 */
    @GetMapping("/v1/payments/{paymentId}")
    public String applyPayment(
            @PathVariable Long paymentId,
            Model model
    ) {
        ApplyPaymentReq applyPaymentReq = paymentService.applyPayment(paymentId);
        model.addAttribute("paymentRequest", applyPaymentReq);
        return "payment";
    }
}
