package payment.controller;

import api.buyhood.domain.auth.entity.AuthUser;
import payment.dto.request.PaymentReq;
import payment.dto.response.PaymentRes;
import payment.service.PaymentService;
import api.buyhood.global.common.dto.Response;
import com.siot.IamportRestClient.exception.IamportResponseException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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
            @RequestBody PaymentReq paymentReq
    ) throws IamportResponseException, IOException {
        return Response.ok(paymentService.preparePayment(authUser, orderId, paymentReq));
    }
}
