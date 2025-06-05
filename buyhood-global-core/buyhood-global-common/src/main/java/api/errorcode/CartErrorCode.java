package api.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CartErrorCode implements ErrorCode {
	//에러코드 3100번대
	NOT_FOUND_CART(3101, "카트가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
	DUPLICATE_CART(3102, "중복된 카트 입니다.", HttpStatus.CONFLICT),
	MULTIPLE_STORE_NOT_ALLOWED(3103, "장바구니에는 서로 다른 가게의 상품을 함께 담을 수 없습니다.", HttpStatus.BAD_REQUEST);

	private final int code;
	private final String message;
	private final HttpStatus status;
}
