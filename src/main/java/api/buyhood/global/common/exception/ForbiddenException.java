package api.buyhood.global.common.exception;

import api.buyhood.global.common.exception.enums.ErrorCode;

public class ForbiddenException extends BaseException {

	public ForbiddenException(ErrorCode errorCode) {
		super(errorCode);
	}
}
