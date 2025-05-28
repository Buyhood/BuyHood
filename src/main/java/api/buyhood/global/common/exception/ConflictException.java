package api.buyhood.global.common.exception;

import api.buyhood.global.common.exception.enums.ErrorCode;

public class ConflictException extends BaseException {

	public ConflictException(ErrorCode errorCode) {
		super(errorCode);
	}
}
