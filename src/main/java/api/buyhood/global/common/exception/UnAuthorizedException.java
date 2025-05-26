package api.buyhood.global.common.exception;

import api.buyhood.global.common.exception.enums.ErrorCode;

public class UnAuthorizedException extends BaseException {

	public UnAuthorizedException(ErrorCode errorCode) {
		super(errorCode);
	}
}
