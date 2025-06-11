package api.buyhood.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {
	//에러코드 4100번대
	JSON_PARSING_FAILED(4101, "JSON 파싱 실패", HttpStatus.INTERNAL_SERVER_ERROR),
	REDIS_SERIALIZE_FAILED(4102, "Redis 정보 직렬화 실패", HttpStatus.INTERNAL_SERVER_ERROR),
	INVALID_TIME_FORMAT(4103, "잘못된 시간 문자열 값입니다.", HttpStatus.BAD_REQUEST),
	NOT_DEFINED_FEIGN_EXCEPTION(4151, "정의되지 않은 FeignClient 예외가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

	private final int code;
	private final String message;
	private final HttpStatus status;
}
