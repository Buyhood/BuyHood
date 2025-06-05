package api.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SellerErrorCode implements ErrorCode {
	//삭제될 도메인
	SELLER_INVALID_PASSWORD(1502, "비밀번호가 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
	SELLER_NOT_FOUND(1501, "해당 사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

	private final int code;
	private final String message;
	private final HttpStatus status;
}
