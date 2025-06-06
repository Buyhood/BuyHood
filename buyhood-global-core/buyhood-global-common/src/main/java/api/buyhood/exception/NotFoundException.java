package api.buyhood.exception;

import api.buyhood.errorcode.ErrorCode;

public class NotFoundException extends BaseException {

	public NotFoundException(ErrorCode errorCode) {
		super(errorCode);
	}
}
