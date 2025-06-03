package payment.controller;

import payment.dto.request.ApplyPaymentReq;
import payment.service.PaymentService;


@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class PaymentController {

    private final PaymentService paymentService;

    /* 결제 요청 */
    @PostMapping("/v1/payments/{paymentId}")
    public String applyPayment(
            @PathVariable Long paymentId,
            Model model
    ) {
        ApplyPaymentReq applyPaymentReq = paymentService.applyPayment(paymentId);
        model.addAttribute("paymentRequest", applyPaymentReq);
        return "payment";
    }
}
