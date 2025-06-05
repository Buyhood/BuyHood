package api.buyhood.global.common.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderErrorCode implements ErrorCode {
	NOT_OWNER_OF_STORE(3003, "자신의 스토어 주문이 아닙니다.", HttpStatus.FORBIDDEN),
	NOT_FOUND_ORDER(3000, "주문이 존재하지 않습니다.", HttpStatus.NOT_FOUND);

	private final int code;
	private final String message;
	private final HttpStatus status;
}
