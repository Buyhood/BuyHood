package api.buyhood.global.common.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
	//에러코드 1400번대 사용예정
	INVALID_USER_ROLE(1401, "유효하지 않은 UserRole 입니다.", HttpStatus.UNAUTHORIZED);

	private final int code;
	private final String message;
	private final HttpStatus status;
}
