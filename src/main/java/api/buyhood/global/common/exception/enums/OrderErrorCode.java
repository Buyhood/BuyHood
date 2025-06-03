package api.buyhood.global.common.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderErrorCode implements ErrorCode {
	NOT_OWNER_OF_STORE(3003, "자신의 스토어 주문이 아닙니다.", HttpStatus.FORBIDDEN),
	NOT_FOUND_ORDER(3000, "주문이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
	NOT_PENDING(3100, "결제 준비 상태가 아니라 결제를 요청할 수 없습니다.",HttpStatus.BAD_REQUEST);

	private final int code;
	private final String message;
	private final HttpStatus status;
}
