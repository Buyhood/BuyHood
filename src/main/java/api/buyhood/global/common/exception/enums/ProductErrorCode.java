package api.buyhood.global.common.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements ErrorCode {

	STOCK_UPDATE_CONFLICT(2000, "재고 변경 중 발생", HttpStatus.CONFLICT),
	OUT_OF_STOCK(2000, "요청하신 수량보다 재고가 부족합니다.", HttpStatus.BAD_REQUEST),
	NOT_FOUND_PRODUCT(2000, "상품이 존재하지 않습니다.", HttpStatus.NOT_FOUND);

	private final int code;
	private final String message;
	private final HttpStatus status;
}
