package api.buyhood.global.common.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PaymentErrorCode implements ErrorCode {

    NOT_FOUND_PAYMENT(3200, "결제를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    NOT_SUPPORTED_ZERO_PAY(3201, "제로페이는 PG 결제에서 지원되지 않습니다.", HttpStatus.BAD_REQUEST),
    CANNOT_REQUEST_PAYMENT(3101,"결제 준비 상태가 아니라 결제를 요청할 수 없습니다." , HttpStatus.BAD_REQUEST)
    ;

    private final int code;
    private final String message;
    private final HttpStatus status;
}
