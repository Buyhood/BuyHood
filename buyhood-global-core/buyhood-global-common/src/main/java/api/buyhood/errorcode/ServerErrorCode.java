package api.buyhood.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ServerErrorCode implements ErrorCode {
	//에러코드 4200번대
	NOT_FOUND(4205, "없는 요청입니다.", HttpStatus.NOT_FOUND),
	DATABASE_ACCESS_ERROR(4204, "데이터베이스 접근 오류입니다.", HttpStatus.BAD_REQUEST),
	INVALID_INPUT_PARAM(4203, "파라미터 값 오류입니다.", HttpStatus.BAD_REQUEST),
	INVALID_INPUT_VALUE(4202, "입력값이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
	INTERNAL_SERVER_ERROR(4201, "서버 내부 오류입니다.", HttpStatus.INTERNAL_SERVER_ERROR);

	private final int code;
	private final String message;
	private final HttpStatus status;
}
