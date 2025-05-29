package api.buyhood.global.common.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SellerErrorCode implements ErrorCode {
	//에러코드 1500번대 사용 예정
	SELLER_NOT_FOUND(1541, "사업자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

	private final int code;
	private final String message;
	private final HttpStatus status;
}
