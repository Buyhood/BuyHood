package api.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CartErrorCode implements ErrorCode {

	NOT_FOUND_CART(3000, "카트가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
	DUPLICATE_CART(3100, "중복된 카트 입니다.", HttpStatus.CONFLICT);

	private final int code;
	private final String message;
	private final HttpStatus status;
}
