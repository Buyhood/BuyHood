package api.buyhood.global.common.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum StoreErrorCode implements ErrorCode {

	STORE_NOT_FOUND(2000, "가게가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
	DUPLICATE_STORE_NAME(3901, "중복된 가게 이름 입니다.", HttpStatus.CONFLICT),
	STORE_NAME_SAME_AS_OLD(3902, "이전과 동일한 가게 이름 입니다.", HttpStatus.CONFLICT),
	;

	private final int code;
	private final String message;
	private final HttpStatus status;
}
