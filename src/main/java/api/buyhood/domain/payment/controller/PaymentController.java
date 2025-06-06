package api.buyhood.domain.payment.controller;

import api.buyhood.domain.payment.dto.response.ApplyPaymentRes;
import api.buyhood.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
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

    /* 결제 요청 (portone) */
    @GetMapping("/v1/payments/{paymentId}")
    public String applyPayment(
            @PathVariable Long paymentId,
            Model model
    ) {
        ApplyPaymentRes applyPaymentRes = paymentService.applyPayment(paymentId);
        model.addAttribute("paymentRequest", applyPaymentRes);
        return "payment-portone";
    }
}
