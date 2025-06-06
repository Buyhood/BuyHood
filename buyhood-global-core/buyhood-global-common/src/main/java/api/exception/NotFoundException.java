package api.exception;

import api.errorcode.ErrorCode;

public class NotFoundException extends BaseException {

	public NotFoundException(ErrorCode errorCode) {
		super(errorCode);
	}
}
