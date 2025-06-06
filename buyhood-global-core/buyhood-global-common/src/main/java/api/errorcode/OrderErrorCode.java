package api.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderErrorCode implements ErrorCode {
	NOT_OWNER_OF_STORE(3003, "자신의 스토어 주문이 아닙니다.", HttpStatus.FORBIDDEN),
	NOT_OWNER_OF_ORDER(3004, "자신의 주문이 아닙니다.", HttpStatus.BAD_REQUEST),
	NOT_FOUND_ORDER(3000, "주문이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
	NOT_PENDING(3100, "결제 준비 상태가 아니라 결제를 요청할 수 없습니다.",HttpStatus.BAD_REQUEST),
	ALREADY_ACCEPTED(3200, "이미 승인된 주문입니다.", HttpStatus.BAD_REQUEST),
	CANNOT_ACCEPT_ORDER(3300, "결제되지 않은 주문은 승인하지 못합니다.", HttpStatus.BAD_REQUEST)
	;

	private final int code;
	private final String message;
	private final HttpStatus status;
}
