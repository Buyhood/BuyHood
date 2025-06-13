package api.buyhood.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PaymentErrorCode implements ErrorCode {
	//NOT_FOUND
	NOT_FOUND_PAYMENT(3300, "결제를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

	//BAD_REQUEST
	NOT_SUPPORTED_ZERO_PAY(3310, "제로페이는 PG 결제에서 지원되지 않습니다.", HttpStatus.BAD_REQUEST),
	CANNOT_REQUEST_PAYMENT(3311, "결제 준비 상태가 아니라 결제를 요청할 수 없습니다.", HttpStatus.BAD_REQUEST),
	NOT_MATCH_ACCOUNT(3312, "결제 금액이 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
	FAILED_PAID(3313, "결제 처리에 실패하였습니다.", HttpStatus.BAD_REQUEST),
	ALREADY_PAID(3314, "이미 처리된 결제입니다.", HttpStatus.BAD_REQUEST),
	INVALID_PAYMENT_METHOD_FOR_ZERO_PAY(3315, "요청한 결제 방식은 제로페이에서 지원되지 않습니다.", HttpStatus.BAD_REQUEST),
	NOT_OWNER_OF_PAYMENT(3316, "자신의 결제가 아닙니다.", HttpStatus.BAD_REQUEST),

	//UNAUTHORIZED
	NOT_MATCH_MERCHANT_UID(3320, "MerchantUid가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),

	//INTERNAL_SERVER_ERROR
	FAILED_CANCEL(3330, "결체 취소에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
	FAILED_CREATE_QR(3331, "QR 생성에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
	INTERNAL_IAM_PORT_ERROR(3332, "iamport 관련 오류입니다.", HttpStatus.INTERNAL_SERVER_ERROR)
	;

	private final int code;
	private final String message;
	private final HttpStatus status;
}
