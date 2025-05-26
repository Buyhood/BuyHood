package api.buyhood.global.common.exception;

import api.buyhood.global.common.exception.enums.ErrorCode;

public class UnAuthorizedException extends BaseException {

	protected UnAuthorizedException(ErrorCode errorCode) {
		super(errorCode);
	}
}
