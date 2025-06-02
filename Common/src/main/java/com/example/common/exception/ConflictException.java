package com.example.common.exception;


import com.example.common.exception.enums.ErrorCode;

public class ConflictException extends BaseException {

	public ConflictException(ErrorCode errorCode) {
		super(errorCode);
	}
}
