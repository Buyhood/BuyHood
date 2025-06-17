package api.buyhood.payment.service;

import api.buyhood.dto.order.OrderFeignDto;
import api.buyhood.dto.user.UserFeignDto;
import api.buyhood.exception.InvalidRequestException;
import api.buyhood.exception.NotFoundException;
import api.buyhood.payment.client.OrderFeignClient;
import api.buyhood.payment.client.UserFeignClient;
import api.buyhood.payment.dto.request.PaymentReq;
import api.buyhood.payment.dto.request.ValidPaymentReq;
import api.buyhood.payment.dto.request.ZPayValidationReq;
import api.buyhood.payment.dto.response.ApplyPaymentRes;
import api.buyhood.payment.dto.response.PaymentRes;
import api.buyhood.payment.entity.Payment;
import api.buyhood.payment.enums.PGProvider;
import api.buyhood.payment.enums.PayStatus;
import api.buyhood.payment.repository.PaymentRepository;
import api.buyhood.security.AuthUser;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.request.PrepareData;
import com.siot.IamportRestClient.response.IamportResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static api.buyhood.errorcode.OrderErrorCode.NOT_OWNER_OF_ORDER;
import static api.buyhood.errorcode.OrderErrorCode.NOT_PENDING;
import static api.buyhood.errorcode.PaymentErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final UserFeignClient userFeignClient;
    private final OrderFeignClient orderFeignClient;
    private final PaymentRepository paymentRepository;
    private final IamportClient iamportClient;

    @Transactional
    public PaymentRes preparePayment(AuthUser authUser, Long orderId, PaymentReq paymentReq) {
        UserFeignDto user = userFeignClient.getRoleUserOrElseThrow(authUser.getId());
        OrderFeignDto order = orderFeignClient.findOrder(orderId);

        if (!order.getUserId().equals(user.getId())) {
            throw new InvalidRequestException(NOT_OWNER_OF_ORDER);
        }

        if (!"PENDING".equals(order.getOrderState())) {
            throw new InvalidRequestException(NOT_PENDING);
        }

        if (paymentRepository.existsByOrderId(order.getOrderId())) {
            throw new InvalidRequestException(ALREADY_VALID_PAYMENT);
        }

        //결제 고유번호
        String merchantUid = String.valueOf(UUID.randomUUID());
        validateZeroPayConsistency(paymentReq, order.getPaymentMethod());

        Payment payment = Payment.of(order.getOrderId(), paymentReq.getPg(), authUser.getEmail(), order.getTotalPrice(),
                merchantUid);
        paymentRepository.save(payment);

        //PG사 결제일 경우
        if (paymentReq.getPg() != PGProvider.ZERO_PAY) {
            try {
                PrepareData prepareData = new PrepareData(payment.getMerchantUid(), payment.getTotalPrice());
                iamportClient.postPrepare(prepareData);

            } catch (IamportResponseException | IOException e) {
                log.info("Failed postPrepare", e);
                throw new InvalidRequestException(INTERNAL_IAM_PORT_ERROR);
            }
        }

        return PaymentRes.of(payment.getId(), orderId, payment.getPg(), payment.getBuyerEmail(),
                payment.getTotalPrice(), payment.getPayStatus());
    }

    @Transactional(readOnly = true)
    public ApplyPaymentRes applyPayment(Long paymentId) {
        Payment payment = paymentRepository.findNotDeletedById(paymentId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_PAYMENT));

        OrderFeignDto order = orderFeignClient.findOrderForApplyPayment(payment.getOrderId());

        if (PayStatus.PAID.equals(payment.getPayStatus())) {
            throw new InvalidRequestException(CANNOT_REQUEST_PAYMENT);
        }

        return ApplyPaymentRes.of(
                payment.getPg().getName(),
                order.getName(),
                order.getPaymentMethod(),
                payment.getMerchantUid(),
                payment.getTotalPrice(),
                payment.getBuyerEmail());
    }

    @Transactional(noRollbackFor = InvalidRequestException.class)
    public void validPayment(Long paymentId, ValidPaymentReq validPaymentReq) {
        Payment payment = paymentRepository.findNotDeletedById(paymentId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_PAYMENT));

        if (payment.isPaid()) {
            throw new InvalidRequestException(ALREADY_PAID);
        }

        //IamportRestClient 관련 Payment 도메인
        try {
            IamportResponse<com.siot.IamportRestClient.response.Payment> response = iamportClient.paymentByImpUid(
                    validPaymentReq.getImpUid());
            com.siot.IamportRestClient.response.Payment iamportPayment = response.getResponse();

            if (!"paid".equals(iamportPayment.getStatus())) {
                payment.failPayment();
                throw new InvalidRequestException(FAILED_PAID);
            }

            if (!payment.getMerchantUid().equals(iamportPayment.getMerchantUid())) {
                payment.failPayment();

                refund(validPaymentReq);
                throw new InvalidRequestException(NOT_MATCH_MERCHANT_UID);
            }

            if (payment.getTotalPrice().compareTo(iamportPayment.getAmount()) != 0) {
                payment.failPayment();

                refund(validPaymentReq);
                throw new InvalidRequestException(NOT_MATCH_ACCOUNT);
            }

        } catch (IamportResponseException | IOException e) {
            log.info("Failed postPrepare", e);
            throw new InvalidRequestException(INTERNAL_IAM_PORT_ERROR);
        }

        payment.successPayment();
    }

    @Transactional(noRollbackFor = InvalidRequestException.class)
    public void validPaymentWithZeroPay(Long paymentId, ZPayValidationReq zPayValidationReq) {
        Payment payment = paymentRepository.findNotDeletedById(paymentId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_PAYMENT));

        if (payment.isPaid()) {
            throw new InvalidRequestException(ALREADY_PAID);
        }

        if (!"paid".equals(zPayValidationReq.getStatus())) {
            payment.failPayment();
            throw new InvalidRequestException(FAILED_PAID);
        }

        if (!payment.getMerchantUid().equals(zPayValidationReq.getMerchantUid())) {
            payment.failPayment();
            throw new InvalidRequestException(NOT_MATCH_MERCHANT_UID);
        }

        if (payment.getTotalPrice().compareTo(zPayValidationReq.getTotalPrice()) != 0) {
            payment.failPayment();

            throw new InvalidRequestException(NOT_MATCH_ACCOUNT);
        }

        payment.successPayment();
    }

    @Transactional
    public byte[] createQR(Long paymentId) {

        Payment payment = paymentRepository.findNotDeletedById(paymentId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_PAYMENT));

        OrderFeignDto order = orderFeignClient.findOrderForApplyPayment(payment.getOrderId());

        if (payment.isPaid()) {
            throw new InvalidRequestException(ALREADY_PAID);
        }

        if (!"ZERO_PAY".equals(order.getPaymentMethod())) {
            throw new InvalidRequestException(INVALID_PAYMENT_METHOD_FOR_ZERO_PAY);
        }

        try {
            int width = 200, height = 200;

            // QR코드 생성 옵션 설정
            Map<EncodeHintType, Object> hintMap = new HashMap<>();
            hintMap.put(EncodeHintType.MARGIN, 0);
            hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            // QR 코드 생성
            String link = String.format("http://localhost:8088/api/v1/payments/%d", paymentId);
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(link, BarcodeFormat.QR_CODE, width, height, hintMap);

            // QR 코드 이미지 생성
            BufferedImage qrCodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

            // QR 코드 이미지를 바이트 배열로 변환, byteArrayOutputStream 에 저장
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(qrCodeImage, "png", byteArrayOutputStream);
            byteArrayOutputStream.flush();

            byte[] qrCodeBytes = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();

            return qrCodeBytes;

        } catch (Exception e) {
            log.info("QRCode Error", e);
            throw new InvalidRequestException(FAILED_CREATE_QR);
        }
    }

    public ApplyPaymentRes applyPaymentWithZeroPay(Long paymentId) {
        Payment payment = paymentRepository.findNotDeletedById(paymentId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_PAYMENT));

        OrderFeignDto order = orderFeignClient.findOrderForApplyPayment(payment.getOrderId());

        return ApplyPaymentRes.of(
                String.valueOf(payment.getPg()),
                order.getName(),
                String.valueOf(order.getPaymentMethod()),
                payment.getMerchantUid(),
                payment.getTotalPrice(),
                payment.getBuyerEmail());
    }

    private void refund(ValidPaymentReq validPaymentReq) {

        try {
            CancelData cancelData = new CancelData(validPaymentReq.getImpUid(), true);
            iamportClient.cancelPaymentByImpUid(cancelData);
        } catch (InvalidRequestException | IOException | IamportResponseException e) {
            log.info("Failed cancelPayment", e);
            throw new InvalidRequestException(INTERNAL_IAM_PORT_ERROR);
        }
    }

    private void validateZeroPayConsistency(PaymentReq paymentReq, String paymentMethod) {
        // 요청이 제로페이인 경우: 기존 주문도 제로페이인지 확인
        if (!PGProvider.ZERO_PAY.equals(paymentReq.getPg()) && "ZERO_PAY".equals(
                paymentMethod)) {
            throw new InvalidRequestException(NOT_SUPPORTED_ZERO_PAY);
        }

        // 요청이 PG사인 경우: 기존 주문도 PG사가 제공하는 결제 방식인지 확인
        if (PGProvider.ZERO_PAY.equals(paymentReq.getPg()) && !"ZERO_PAY".equals(
                paymentMethod)) {
            throw new InvalidRequestException(INVALID_PAYMENT_METHOD_FOR_ZERO_PAY);
        }
    }
}
