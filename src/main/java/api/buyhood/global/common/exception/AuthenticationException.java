package api.buyhood.global.common.exception;

import api.buyhood.global.common.exception.enums.ErrorCode;

public class AuthenticationException extends BaseException {

	public AuthenticationException(ErrorCode errorCode) {
		super(errorCode);
	}
}
