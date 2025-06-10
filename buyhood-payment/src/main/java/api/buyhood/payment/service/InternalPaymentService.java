package api.buyhood.payment.service;

import api.buyhood.dto.payment.PaymentFeignDto;
import api.buyhood.exception.NotFoundException;
import api.buyhood.payment.entity.Payment;
import api.buyhood.payment.repository.InternalPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static api.buyhood.errorcode.PaymentErrorCode.NOT_FOUND_PAYMENT;

@Service
@RequiredArgsConstructor
public class InternalPaymentService {

    private final InternalPaymentRepository internalPaymentRepository;

    @Transactional(readOnly = true)
    public PaymentFeignDto findPaymentByOrderId(Long orderId) {
        Payment payment = internalPaymentRepository.findNotDeletedByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_PAYMENT));

        return new PaymentFeignDto(
                payment.getId(),
                payment.getOrder().getId(),
                String.valueOf(payment.getPg()),
                payment.getBuyerEmail(),
                payment.getTotalPrice(),
                String.valueOf(payment.getPayStatus()),
                payment.getMerchantUid()
        );
    }

    public void refundPayment(Long paymentId) {
        Payment payment = internalPaymentRepository.findNotDeletedById(paymentId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_PAYMENT));

        payment.cancel();
    }
}
