package com.example.common.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ServerErrorCode implements ErrorCode {
	//8000번대 사용 예정
	NOT_FOUND(8005, "존재하지 않는 API 요청입니다.", HttpStatus.NOT_FOUND),
	DATABASE_ACCESS_ERROR(8004, "DB 접근 중 오류 발생", HttpStatus.INTERNAL_SERVER_ERROR),
	INVALID_INPUT_PARAM(8003, "잘못된 파라미터 입력입니다.", HttpStatus.BAD_REQUEST),
	INTERNAL_SERVER_ERROR(8001, "서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
	INVALID_INPUT_VALUE(8002, "입력값이 유효하지 않습니다", HttpStatus.BAD_REQUEST);
	private final int code;
	private final String message;
	private final HttpStatus status;
}
