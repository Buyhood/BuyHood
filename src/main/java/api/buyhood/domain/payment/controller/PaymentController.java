package api.buyhood.domain.payment.controller;

import api.buyhood.domain.payment.dto.request.ApplyPaymentReq;
import api.buyhood.domain.payment.service.PaymentService;
import api.buyhood.global.common.dto.Response;
import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.Base64;


@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class PaymentController {

    private final PaymentService paymentService;

    /* 결제 요청 (portone) */
    @GetMapping("/v1/payments/{paymentId}/portone")
    public String applyPayment(
            @PathVariable Long paymentId,
            Model model
    ) {
        ApplyPaymentReq applyPaymentReq = paymentService.applyPayment(paymentId);
        model.addAttribute("paymentRequest", applyPaymentReq);
        return "payment-portone";
    }

    /* 결제를 위한 QR 요청*/
    @GetMapping("/v1/payments/{paymentId}/zeropay")
    public String createQR(
            @PathVariable Long paymentId,
            Model model
    )  throws IOException, WriterException {
        byte[] qrImage = paymentService.createQR(paymentId);
        String base64Qr = Base64.getEncoder().encodeToString(qrImage);
        model.addAttribute("qrImage", base64Qr);
        return "payment-qr";
    }

    /* 결제 요청 (제로페이) */
    @GetMapping("/v1/payments/{paymentId}")
    public String applyPaymentWithZeroPay(
            @PathVariable Long paymentId,
            Model model
    ) {
        ApplyPaymentReq applyPaymentReq = paymentService.applyPaymentWithZeroPay(paymentId);
        model.addAttribute("paymentRequest", applyPaymentReq);
        return "payment-zeropay";
    }

}
