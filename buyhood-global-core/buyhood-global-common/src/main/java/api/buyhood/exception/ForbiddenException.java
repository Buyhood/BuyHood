package api.buyhood.exception;

import api.buyhood.errorcode.ErrorCode;

public class ForbiddenException extends BaseException {

	public ForbiddenException(ErrorCode errorCode) {
		super(errorCode);
	}
}
