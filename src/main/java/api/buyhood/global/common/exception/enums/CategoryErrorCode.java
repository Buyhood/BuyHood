package api.buyhood.global.common.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CategoryErrorCode implements ErrorCode {

	DUPLICATE_CATEGORIES(1400, "중복된 카테고리 입니다.", HttpStatus.CONFLICT);

	private final int code;
	private final String message;
	private final HttpStatus status;
}
