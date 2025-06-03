package api.exception;

import api.errorcode.ErrorCode;

public class InvalidRequestException extends BaseException {

	public InvalidRequestException(ErrorCode errorCode) {
		super(errorCode);
	}
}
