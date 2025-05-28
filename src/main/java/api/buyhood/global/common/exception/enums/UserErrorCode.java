package api.buyhood.global.common.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
	//에러코드 1400번대 사용예정
	INVALID_NEW_PASSWORD_FORMAT(1405, "새 비밀번호는 8자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다.", HttpStatus.BAD_REQUEST),
	PASSWORD_SAME_AS_OLD(1404, "기존 비밀번호와 같습니다.", HttpStatus.BAD_REQUEST),
	INVALID_PASSWORD(1403, "비밀번호가 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
	USER_NOT_FOUND(1402, "해당 유저를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	INVALID_USER_ROLE(1401, "유효하지 않은 UserRole 입니다.", HttpStatus.UNAUTHORIZED);

	private final int code;
	private final String message;
	private final HttpStatus status;
}
