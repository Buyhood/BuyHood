package api.exception;

import api.errorcode.ErrorCode;
import lombok.Getter;

@Getter
public abstract class BaseException extends RuntimeException {

	private final ErrorCode errorCode;
	
	protected BaseException(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}

}
