package com.example.common.exception;

import com.example.common.exception.enums.ErrorCode;

public class ForbiddenException extends BaseException {

	public ForbiddenException(ErrorCode errorCode) {
		super(errorCode);
	}
}
