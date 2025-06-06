package api.exception;

import api.errorcode.ErrorCode;

public class AuthenticationException extends BaseException {

	public AuthenticationException(ErrorCode errorCode) {
		super(errorCode);
	}
}
