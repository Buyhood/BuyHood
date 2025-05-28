package api.buyhood.global.common.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {
	//에러코드 1300번대 사용 예정
	USER_NOT_FOUND(1303, "해당 회원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	INVALID_TOKEN(1301, "만료된 토큰입니다.", HttpStatus.UNAUTHORIZED),
	EMAIL_DUPLICATED(1302, "이미 가입된 이메일입니다.", HttpStatus.CONFLICT);

	private final int code;
	private final String message;
	private final HttpStatus status;
}
