package api.buyhood.global.common.exception;

import api.buyhood.global.common.exception.enums.ErrorCode;

public class NotFoundException extends BaseException {

	public NotFoundException(ErrorCode errorCode) {
		super(errorCode);
	}
}
