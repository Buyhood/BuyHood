package api.buyhood.global.common.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {

	JSON_PARSING_FAILED(4100, "JSON 파싱 실패", HttpStatus.INTERNAL_SERVER_ERROR),
	REDIS_SERIALIZE_FAILED(4000, "Redis 정보 직렬화 실패", HttpStatus.INTERNAL_SERVER_ERROR);

	private final int code;
	private final String message;
	private final HttpStatus status;
}
