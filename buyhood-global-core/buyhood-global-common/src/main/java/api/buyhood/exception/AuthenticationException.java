package api.buyhood.exception;

import api.buyhood.errorcode.ErrorCode;

public class AuthenticationException extends BaseException {

	public AuthenticationException(ErrorCode errorCode) {
		super(errorCode);
	}
}
