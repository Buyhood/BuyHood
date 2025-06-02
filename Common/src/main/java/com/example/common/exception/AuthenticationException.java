package com.example.common.exception;

import com.example.common.exception.enums.ErrorCode;

public class AuthenticationException extends BaseException {

	public AuthenticationException(ErrorCode errorCode) {
		super(errorCode);
	}
}
