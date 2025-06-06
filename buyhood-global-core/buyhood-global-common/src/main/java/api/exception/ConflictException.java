package api.exception;

import api.errorcode.ErrorCode;

public class ConflictException extends BaseException {

	public ConflictException(ErrorCode errorCode) {
		super(errorCode);
	}
}
