package api.buyhood.global.common.exception;

import api.buyhood.global.common.exception.enums.ErrorCode;
import lombok.Getter;

@Getter
public abstract class BaseException extends RuntimeException {

	private final ErrorCode errorCode;
	
	protected BaseException(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}

}
