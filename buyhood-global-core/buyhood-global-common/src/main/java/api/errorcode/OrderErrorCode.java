package api.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderErrorCode implements ErrorCode {
	//에러코드 3200번대
	NOT_OWNER_OF_STORE(3201, "자신의 스토어 주문이 아닙니다.", HttpStatus.FORBIDDEN),
	NOT_FOUND_ORDER(3202, "주문이 존재하지 않습니다.", HttpStatus.NOT_FOUND);

	private final int code;
	private final String message;
	private final HttpStatus status;
}
