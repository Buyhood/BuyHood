package api.buyhood.payment.controller;

import api.buyhood.payment.dto.response.ApplyPaymentRes;
import api.buyhood.payment.service.PaymentService;
import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Base64;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class PaymentController {

    private final PaymentService paymentService;

    /* 결제 요청 (portone) */
    @GetMapping("/v1/payments/{paymentId}/portone")
    public String applyPayment(@PathVariable Long paymentId, Model model) {
        ApplyPaymentRes applyPaymentRes = paymentService.applyPayment(paymentId);
        model.addAttribute("paymentRequest", applyPaymentRes);
        return "payment-portone";
    }

    /* 결제를 위한 QR 요청*/
    @GetMapping("/v1/payments/{paymentId}/qr")
    public String createQR(@PathVariable Long paymentId, Model model) {
        byte[] qrImage = paymentService.createQR(paymentId);
        String base64Qr = Base64.getEncoder().encodeToString(qrImage);
        model.addAttribute("qrImage", base64Qr);
        return "payment-qr";
    }

    /* 결제 요청 (제로페이)
     *
     * qr코드 링크를 통해 호출되는 api입니다.
     *  */
    @GetMapping("/v1/payments/{paymentId}")
    public String applyPaymentWithZeroPay(@PathVariable Long paymentId, Model model) {
        ApplyPaymentRes applyPaymentRes = paymentService.applyPaymentWithZeroPay(paymentId);
        model.addAttribute("paymentRequest", applyPaymentRes);
        return "payment-zeropay";
    }

}