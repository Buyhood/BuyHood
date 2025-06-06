package api.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PaymentErrorCode implements ErrorCode {

    NOT_FOUND_PAYMENT(3200, "결제를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    NOT_SUPPORTED_ZERO_PAY(3201, "제로페이는 PG 결제에서 지원되지 않습니다.", HttpStatus.BAD_REQUEST),
    CANNOT_REQUEST_PAYMENT(3101,"결제 준비 상태가 아니라 결제를 요청할 수 없습니다." , HttpStatus.BAD_REQUEST),
    NOT_MATCHE_MERCHANT_UID(3102,"MerchantUid가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),
    NOT_MATCHE_ACCOUNT(3103, "결제 금액이 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    FAILED_PAID(3014, "결제 처리에 실패하였습니다.", HttpStatus.BAD_REQUEST),
    ALREADY_PAID(3015, "이미 처리된 결제입니다.", HttpStatus.BAD_REQUEST)
    ;

    private final int code;
    private final String message;
    private final HttpStatus status;
}
