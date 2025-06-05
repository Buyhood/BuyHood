package api.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements ErrorCode {
	//에러코드 2100번대
	STOCK_UPDATE_CONFLICT(2100, "재고 변경 중 발생", HttpStatus.CONFLICT),
	OUT_OF_STOCK(2101, "요청하신 수량보다 재고가 부족합니다.", HttpStatus.BAD_REQUEST),
	PRODUCT_NAME_SAME_AS_OLD(2102, "상품 이름이 이전과 동일합니다.", HttpStatus.BAD_REQUEST),
	PRODUCT_NOT_FOUND(2103, "상품을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	DUPLICATE_PRODUCT_NAME(2104, "중복된 상품 이름 입니다.", HttpStatus.CONFLICT);

	private final int code;
	private final String message;
	private final HttpStatus status;
}
