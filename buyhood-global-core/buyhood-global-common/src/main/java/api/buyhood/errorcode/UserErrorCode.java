package api.buyhood.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
	//에러코드 1200번대 사용예정
	BUSINESS_NUMBER_DUPLICATED(1211, "이미 등록된 사업자 등록번호입니다.", HttpStatus.CONFLICT),
	ALREADY_SELLER(1212, "이미 SELLER 권한이 있습니다.", HttpStatus.BAD_REQUEST),
	BUSINESS_NUMBER_ALREADY_REGISTERED(1213, "이미 사업자 등록번호가 등록되어 있습니다.", HttpStatus.CONFLICT),
	PASSWORD_SAME_AS_OLD(1204, "기존 비밀번호와 같습니다.", HttpStatus.BAD_REQUEST),
	USER_INVALID_PASSWORD(1203, "비밀번호가 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
	USER_NOT_FOUND(1202, "해당 유저를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	INVALID_USER_ROLE(1201, "유효하지 않은 UserRole 입니다.", HttpStatus.UNAUTHORIZED);

	private final int code;
	private final String message;
	private final HttpStatus status;
}
