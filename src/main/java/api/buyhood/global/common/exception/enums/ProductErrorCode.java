package api.buyhood.global.common.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements ErrorCode {

	PRODUCT_NOT_FOUND(2100, "상품을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	;

	private final int code;
	private final String message;
	private final HttpStatus status;
}
