package com.example.common.exception;


import com.example.common.exception.enums.ErrorCode;

public class NotFoundException extends BaseException {

	public NotFoundException(ErrorCode errorCode) {
		super(errorCode);
	}
}
