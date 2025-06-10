package api.buyhood.exception;

import api.buyhood.errorcode.ErrorCode;

public class FilterAuthenticationException extends BaseException {

	public FilterAuthenticationException(ErrorCode errorCode) {
		super(errorCode);
	}
}
