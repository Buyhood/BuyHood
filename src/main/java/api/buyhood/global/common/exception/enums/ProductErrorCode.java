package api.buyhood.global.common.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements ErrorCode {

	STOCK_UPDATE_CONFLICT(2000, "재고 변경 중 발생", HttpStatus.CONFLICT),
	OUT_OF_STOCK(2000, "요청하신 수량보다 재고가 부족합니다.", HttpStatus.BAD_REQUEST),
	PRODUCT_NAME_SAME_AS_OLD(2001, "상품 이름이 이전과 동일합니다.", HttpStatus.BAD_REQUEST),
	PRODUCT_NOT_FOUND(2401, "상품을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	DUPLICATE_PRODUCT_NAME(2901, "중복된 상품 이름 입니다.", HttpStatus.CONFLICT);

	private final int code;
	private final String message;
	private final HttpStatus status;
}
