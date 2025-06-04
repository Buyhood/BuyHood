package api.buyhood.domain.payment.service;

import api.buyhood.domain.auth.entity.AuthUser;
import api.buyhood.domain.order.entity.Order;
import api.buyhood.domain.order.repository.OrderRepository;
import api.buyhood.domain.payment.dto.request.ApplyPaymentReq;
import api.buyhood.domain.payment.dto.request.PaymentReq;
import api.buyhood.domain.payment.dto.response.PaymentRes;
import api.buyhood.domain.payment.entity.Payment;
import api.buyhood.domain.payment.repository.PaymentRepository;
import api.buyhood.domain.user.entity.User;
import api.buyhood.domain.user.repository.UserRepository;
import api.buyhood.global.common.exception.InvalidRequestException;
import api.buyhood.global.common.exception.NotFoundException;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.PrepareData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

import static api.buyhood.domain.order.enums.OrderStatus.PENDING;
import static api.buyhood.domain.payment.enums.PGProvider.ZERO_PAY;
import static api.buyhood.global.common.exception.enums.OrderErrorCode.*;
import static api.buyhood.global.common.exception.enums.PaymentErrorCode.NOT_FOUND_PAYMENT;
import static api.buyhood.global.common.exception.enums.PaymentErrorCode.NOT_SUPPORTED_ZERO_PAY;
import static api.buyhood.global.common.exception.enums.UserErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final IamportClient iamportClient;

    @Transactional
    public PaymentRes preparePayment(AuthUser authUser, Long orderId, PaymentReq paymentReq) throws IamportResponseException, IOException {
        User user = userRepository.findByEmail(authUser.getEmail())
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

        Order order = orderRepository.findNotDeletedById(orderId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_ORDER));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new InvalidRequestException(NOT_OWNER_OF_STORE);
        }

        if (!PENDING.equals(order.getStatus())) {
            throw new InvalidRequestException(NOT_PENDING);
        }

        //결제 고유번호
        String merchantUid = String.valueOf(UUID.randomUUID());

        Payment payment = Payment.of(order, paymentReq.getPg(), paymentReq.getPaymentMethod(), authUser.getEmail(), order.getTotalPrice(), merchantUid);
        paymentRepository.save(payment);

        //사전 검증 요청 객체
        PrepareData prepareData = new PrepareData(payment.getMerchantUid(), BigDecimal.valueOf(payment.getTotalPrice()));
        iamportClient.postPrepare(prepareData);

        return PaymentRes.of(payment.getId(), orderId, payment.getPg(), payment.getPaymentMethod(), payment.getBuyerEmail(), payment.getTotalPrice(), payment.getPayStatus());
    }

    @Transactional(readOnly = true)
    public ApplyPaymentReq applyPayment(Long paymentId) {
        Payment payment = paymentRepository.findNotDeletedById(paymentId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_PAYMENT));

        if (ZERO_PAY.equals(payment.getPg())) {
            throw new InvalidRequestException(NOT_SUPPORTED_ZERO_PAY);
        }

        return ApplyPaymentReq.of(payment.getPg().getName(), String.valueOf(payment.getPaymentMethod()), payment.getMerchantUid(), BigDecimal.valueOf(payment.getTotalPrice()), payment.getBuyerEmail());
    }
}
