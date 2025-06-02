package com.example.common.exception;


import com.example.common.exception.enums.ErrorCode;
import lombok.Getter;

@Getter
public abstract class BaseException extends RuntimeException {

	private final ErrorCode errorCode;

	protected BaseException(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}

}
