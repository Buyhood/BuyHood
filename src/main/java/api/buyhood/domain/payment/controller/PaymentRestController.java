package api.buyhood.domain.payment.controller;

import api.buyhood.domain.auth.entity.AuthUser;
import api.buyhood.domain.payment.dto.request.PaymentReq;
import api.buyhood.domain.payment.dto.request.ValidPaymentReq;
import api.buyhood.domain.payment.dto.request.ZPayValidationReq;
import api.buyhood.domain.payment.dto.response.PaymentRes;
import api.buyhood.domain.payment.service.PaymentService;
import api.buyhood.global.common.dto.Response;
import com.siot.IamportRestClient.exception.IamportResponseException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Base64;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PaymentRestController {
    private final PaymentService paymentService;

    /* 결제 준비 (사전 검증) */
    @PostMapping("/v1/orders/{orderId}/prepare")
    public Response<PaymentRes> preparePayment(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long orderId,
            @Valid @RequestBody PaymentReq paymentReq
    ) throws IamportResponseException, IOException {
        return Response.ok(paymentService.preparePayment(authUser, orderId, paymentReq));
    }

    /* 결제 후 검증 (postone)*/
    @Secured("ROLE_USER")
    @PostMapping("/v1/payments/{paymentId}")
    public Response<String> validPayment(
            @PathVariable Long paymentId,
            @Valid @RequestBody ValidPaymentReq validPaymentReq
    ) throws IamportResponseException, IOException {
        paymentService.validPayment(paymentId, validPaymentReq);
        return Response.ok("결제가 완료되었습니다.");
    }

    /* 결제 후 검증 (zeropay)*/
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
