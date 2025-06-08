package api.buyhood.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {
	//에러코드 1100번대
	INVALID_TOKEN(1101, "만료된 토큰입니다.", HttpStatus.UNAUTHORIZED),
	SELLER_EMAIL_DUPLICATED(1103, "이미 가입된 이메일입니다.", HttpStatus.CONFLICT),
	USER_EMAIL_DUPLICATED(1102, "이미 가입된 이메일입니다.", HttpStatus.CONFLICT);

	private final int code;
	private final String message;
	private final HttpStatus status;
}
