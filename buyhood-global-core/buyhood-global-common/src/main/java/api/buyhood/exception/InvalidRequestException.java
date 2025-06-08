package api.buyhood.exception;

import api.buyhood.errorcode.ErrorCode;

public class InvalidRequestException extends BaseException {

	public InvalidRequestException(ErrorCode errorCode) {
		super(errorCode);
	}
}
