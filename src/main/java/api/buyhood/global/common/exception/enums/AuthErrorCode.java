package api.buyhood.global.common.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

	INVALID_TOKEN(10, "만료된 토큰입니다.", HttpStatus.UNAUTHORIZED),
	EMAIL_DUPLICATED(9, "이미 가입된 이메일입니다.", HttpStatus.CONFLICT);

	private final int code;
	private final String message;
	private final HttpStatus status;
}
