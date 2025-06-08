package api.buyhood.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {
	//에러코드 4100번대
	JSON_PARSING_FAILED(4101, "JSON 파싱 실패", HttpStatus.INTERNAL_SERVER_ERROR),
	REDIS_SERIALIZE_FAILED(4102, "Redis 정보 직렬화 실패", HttpStatus.INTERNAL_SERVER_ERROR);

	private final int code;
	private final String message;
	private final HttpStatus status;
}
