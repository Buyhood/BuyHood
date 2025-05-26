package api.buyhood.global.common.exception;

import api.buyhood.global.common.exception.enums.ErrorCode;

public class InvalidRequestException extends BaseException {

	public InvalidRequestException(ErrorCode errorCode) {
		super(errorCode);
	}
}
