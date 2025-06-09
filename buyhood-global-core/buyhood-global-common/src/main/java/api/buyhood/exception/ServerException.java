package api.buyhood.exception;

import api.buyhood.errorcode.ErrorCode;

public class ServerException extends BaseException {

	public ServerException(ErrorCode errorCode) {
		super(errorCode);
	}
}
