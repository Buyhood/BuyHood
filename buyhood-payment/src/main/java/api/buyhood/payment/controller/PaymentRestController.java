package api.buyhood.payment.controller;

import api.buyhood.dto.Response;
import api.buyhood.payment.dto.request.PaymentReq;
import api.buyhood.payment.dto.request.ValidPaymentReq;
import api.buyhood.payment.dto.request.ZPayValidationReq;
import api.buyhood.payment.dto.response.PaymentRes;
import api.buyhood.payment.service.PaymentService;
import api.buyhood.security.AuthUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PaymentRestController {

    private final PaymentService paymentService;

    /* 결제 사전 검증 */
    @Secured("ROLE_USER")
    @PostMapping("/v1/orders/{orderId}/prepare")
    public Response<PaymentRes> preparePayment(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long orderId,
            @Valid @RequestBody PaymentReq paymentReq
    ) {
        return Response.ok(paymentService.preparePayment(authUser, orderId, paymentReq));
    }

    /* 결제 후 검증 (portone) */
    @Secured("ROLE_USER")
    @PostMapping("/v1/payments/{paymentId}")
    public Response<String> validPayment(
            @PathVariable Long paymentId,
            @Valid @RequestBody ValidPaymentReq validPaymentReq
    ) {
        paymentService.validPayment(paymentId, validPaymentReq);
        return Response.ok("결제가 완료되었습니다.");
    }

    /* 결제 후 검증 (zeropay) */
    @Secured("ROLE_USER")
    @PostMapping("/v1/payments/{paymentId}/zeropay")
    public Response<String> validPaymentWithZeroPay(
            @PathVariable Long paymentId,
            @Valid @RequestBody ZPayValidationReq zPayValidationReq
    ) {
        paymentService.validPaymentWithZeroPay(paymentId, zPayValidationReq);
        return Response.ok("결제가 완료되었습니다.");
    }
}
