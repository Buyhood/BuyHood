package com.example.common.exception;

import com.example.common.exception.enums.ErrorCode;

public class InvalidRequestException extends BaseException {

	public InvalidRequestException(ErrorCode errorCode) {
		super(errorCode);
	}
}
