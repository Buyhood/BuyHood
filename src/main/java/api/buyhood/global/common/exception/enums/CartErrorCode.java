package api.buyhood.global.common.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CartErrorCode implements ErrorCode {

	NOT_FOUND_CART(3000, "카트가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
	DUPLICATE_CART(3100, "중복된 카트 입니다.", HttpStatus.CONFLICT),
	MULTIPLE_STORE_NOT_ALLOWED(3500, "장바구니에는 서로 다른 가게의 상품을 함께 담을 수 없습니다.", HttpStatus.BAD_REQUEST);

	private final int code;
	private final String message;
	private final HttpStatus status;
}
