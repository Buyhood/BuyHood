package com.example.common.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {
	//에러코드 1300번대 사용 예정
	INVALID_TOKEN(1301, "만료된 토큰입니다.", HttpStatus.UNAUTHORIZED),
	SELLER_EMAIL_DUPLICATED(1304, "이미 가입된 이메일입니다.", HttpStatus.CONFLICT),
	USER_EMAIL_DUPLICATED(1302, "이미 가입된 이메일입니다.", HttpStatus.CONFLICT);

	private final int code;
	private final String message;
	private final HttpStatus status;
}
