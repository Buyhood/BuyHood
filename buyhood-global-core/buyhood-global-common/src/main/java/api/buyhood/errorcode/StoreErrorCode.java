package api.buyhood.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum StoreErrorCode implements ErrorCode {
	//에러코드 2200번대
	STORE_NAME_SAME_AS_OLD(2201, "이전과 동일한 가게 이름 입니다.", HttpStatus.BAD_REQUEST),
	STORE_ADDRESS_SAME_AS_OLD(2202, "이전과 동일한 가게 주소 입니다.", HttpStatus.BAD_REQUEST),

	NOT_STORE_OWNER(2231, "해당 작업을 수행하기 위한 권한이 없습니다.", HttpStatus.FORBIDDEN),

	STORE_NOT_FOUND(2241, "가게가 존재하지 않습니다.", HttpStatus.NOT_FOUND),

	DUPLICATE_STORE_NAME(2291, "중복된 가게 이름 입니다.", HttpStatus.CONFLICT),
	;

	private final int code;
	private final String message;
	private final HttpStatus status;
}
