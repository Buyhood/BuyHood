package api.buyhood.domain.payment.service;

import api.buyhood.domain.auth.entity.AuthUser;
import api.buyhood.domain.order.entity.Order;
import api.buyhood.domain.order.repository.OrderRepository;
import api.buyhood.domain.payment.dto.request.ApplyPaymentReq;
import api.buyhood.domain.payment.dto.request.PaymentReq;
import api.buyhood.domain.payment.dto.request.ValidPaymentReq;
import api.buyhood.domain.payment.dto.request.ZPayValidationReq;
import api.buyhood.domain.payment.dto.response.PaymentRes;
import api.buyhood.domain.payment.entity.Payment;
import api.buyhood.domain.payment.enums.PGProvider;
import api.buyhood.domain.payment.enums.PayStatus;
import api.buyhood.domain.payment.repository.PaymentRepository;
import api.buyhood.domain.user.entity.User;
import api.buyhood.domain.user.repository.UserRepository;
import api.buyhood.global.common.exception.InvalidRequestException;
import api.buyhood.global.common.exception.NotFoundException;
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
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static api.buyhood.domain.order.enums.OrderStatus.PENDING;
import static api.buyhood.domain.order.enums.PaymentMethod.ZERO_PAY;
import static api.buyhood.global.common.exception.enums.OrderErrorCode.*;
import static api.buyhood.global.common.exception.enums.PaymentErrorCode.*;
import static api.buyhood.global.common.exception.enums.UserErrorCode.USER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final IamportClient iamportClient;

    @Transactional(rollbackFor = Exception.class)
    public PaymentRes preparePayment(AuthUser authUser, Long orderId, PaymentReq paymentReq) throws IamportResponseException, IOException {
        User user = userRepository.findByEmail(authUser.getEmail())
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

        Order order = orderRepository.findNotDeletedById(orderId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_ORDER));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new InvalidRequestException(NOT_OWNER_OF_ORDER);
        }

        if (!PENDING.equals(order.getStatus())) {
            throw new InvalidRequestException(NOT_PENDING);
        }

        //결제 고유번호
        String merchantUid = String.valueOf(UUID.randomUUID());

        Payment payment = Payment.of(order, paymentReq.getPg(), authUser.getEmail(), order.getTotalPrice(), merchantUid);
        paymentRepository.save(payment);

       //PG사 결제일 경우
        if (paymentReq.getPg() != PGProvider.ZERO_PAY) {
            //사전 검증 요청 객체
            PrepareData prepareData = new PrepareData(payment.getMerchantUid(), payment.getTotalPrice());
            iamportClient.postPrepare(prepareData);
        }

        return PaymentRes.of(payment.getId(), orderId, payment.getPg(), payment.getBuyerEmail(), payment.getTotalPrice(), payment.getPayStatus());
    }

    @Transactional(readOnly = true)
    public ApplyPaymentReq applyPayment(Long paymentId) {
        Payment payment = paymentRepository.findNotDeletedById(paymentId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_PAYMENT));

        if (!PayStatus.READY.equals(payment.getPayStatus())) {
            throw new InvalidRequestException(CANNOT_REQUEST_PAYMENT);
        }

        if (ZERO_PAY.equals(payment.getOrder().getPaymentMethod())) {
            throw new InvalidRequestException(NOT_SUPPORTED_ZERO_PAY);
        }

        return ApplyPaymentReq.of(
                payment.getPg().getName(),
                payment.getOrder().getName(),
                String.valueOf(payment.getOrder().getPaymentMethod()),
                payment.getMerchantUid(),
                payment.getTotalPrice(),
                payment.getBuyerEmail());
    }

    @Transactional(rollbackFor = Exception.class)
    public void validPayment(Long paymentId, ValidPaymentReq validPaymentReq) throws IamportResponseException, IOException {
        Payment payment = paymentRepository.findNotDeletedById(paymentId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_PAYMENT));

        //로컬 내 Payment 도메인과의 구별을 위해 import를 따로 진행하지 않았습니다.
        IamportResponse<com.siot.IamportRestClient.response.Payment> response = iamportClient.paymentByImpUid(validPaymentReq.getImpUid());
        com.siot.IamportRestClient.response.Payment iamportPayment = response.getResponse();

        if (payment.isPaid()) {
            throw new InvalidRequestException(ALREADY_PAID);
        }

        if (!"paid".equals(iamportPayment.getStatus())) {
            payment.failPayment();
            throw new InvalidRequestException(NOT_PAID);
        }

        if (!payment.getMerchantUid().equals(iamportPayment.getMerchantUid())) {
            payment.failPayment();

            refund(validPaymentReq);
            throw new InvalidRequestException(NOT_MATCHE_MERCHANT_UID);
        }

        if (payment.getTotalPrice().compareTo(iamportPayment.getAmount()) != 0) {
            payment.failPayment();

            refund(validPaymentReq);
            throw new InvalidRequestException(NOT_MATCHE_ACCOUNT);
        }

        payment.successPayment();
    }

    @Transactional
    public void validPaymentWithZeroPay(Long paymentId, ZPayValidationReq zPayValidationReq) {
        Payment payment = paymentRepository.findNotDeletedById(paymentId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_PAYMENT));

        if (payment.isPaid()) {
            throw new InvalidRequestException(ALREADY_PAID);
        }

        if (!"paid".equals(zPayValidationReq.getStatus())) {
            payment.failPayment();
            throw new InvalidRequestException(NOT_PAID);
        }

        if (!payment.getMerchantUid().equals(zPayValidationReq.getMerchantUid())) {
            payment.failPayment();
            throw new InvalidRequestException(NOT_MATCHE_MERCHANT_UID);
        }

        if (payment.getTotalPrice().compareTo(BigDecimal.valueOf(zPayValidationReq.getTotalPrice())) != 0) {
            payment.failPayment();

            throw new InvalidRequestException(NOT_MATCHE_ACCOUNT);
        }

        payment.successPayment();
    }

    @Transactional(rollbackFor = Exception.class)
    public byte[] createQR(Long paymentId) {
        try {
            int width = 200, height = 200;

            // QR코드 생성 옵션 설정
            Map<EncodeHintType, Object> hintMap = new HashMap<>();
            hintMap.put(EncodeHintType.MARGIN, 0);
            hintMap.put(EncodeHintType.CHARACTER_SET,"UTF-8");

            // QR 코드 생성
            String link = String.format("http://localhost:8080/api/v1/payments/%d", paymentId);
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(link, BarcodeFormat.QR_CODE, width, height, hintMap);

            // QR 코드 이미지 생성
            BufferedImage qrCodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

            // QR 코드 이미지를 바이트 배열로 변환, byteArrayOutputStream 에 저장
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(qrCodeImage,"png", byteArrayOutputStream);
            byteArrayOutputStream.flush();

            byte[] qrCodeBytes = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();

            return qrCodeBytes;

        } catch (Exception e) {
            log.info("QRCode Error");
            throw new InvalidRequestException(FAILED_CREATE_QR);
        }
    }

    private void refund(ValidPaymentReq validPaymentReq) throws IamportResponseException, IOException {
        CancelData cancelData = new CancelData(validPaymentReq.getImpUid(), true);
        iamportClient.cancelPaymentByImpUid(cancelData);
    }

    public ApplyPaymentReq applyPaymentWithZeroPay(Long paymentId) {
        Payment payment = paymentRepository.findNotDeletedById(paymentId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_PAYMENT));

        Order order = orderRepository.findNotDeletedById(payment.getOrder().getId())
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_ORDER));

        return ApplyPaymentReq.of(
                String.valueOf(payment.getPg()),
                order.getName(),
                String.valueOf(order.getPaymentMethod()),
                payment.getMerchantUid(),
                payment.getTotalPrice(),
                payment.getBuyerEmail());
    }
}
