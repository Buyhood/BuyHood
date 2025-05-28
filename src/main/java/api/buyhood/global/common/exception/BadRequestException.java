package api.buyhood.global.common.exception;

import api.buyhood.global.common.exception.enums.ErrorCode;

public class BadRequestException extends BaseException {

	public BadRequestException(ErrorCode errorCode) {
		super(errorCode);
	}
}
