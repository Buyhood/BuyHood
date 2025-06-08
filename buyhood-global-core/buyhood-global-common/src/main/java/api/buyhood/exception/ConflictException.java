package api.buyhood.exception;

import api.buyhood.errorcode.ErrorCode;

public class ConflictException extends BaseException {

	public ConflictException(ErrorCode errorCode) {
		super(errorCode);
	}
}
