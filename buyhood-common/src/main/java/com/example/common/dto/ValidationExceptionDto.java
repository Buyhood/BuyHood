package com.example.common.dto;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ValidationExceptionDto {

	private final int code;
	private final String message;
	private final List<FieldError> fieldErrors;

	@Getter
	@RequiredArgsConstructor
	public static class FieldError {

		private final String field;
		private final Object rejectedValue;
		private final String reason;
	}
}
