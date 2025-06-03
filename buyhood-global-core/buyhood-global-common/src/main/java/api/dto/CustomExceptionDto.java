package api.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomExceptionDto {

	private final int code;
	private final String message;

}
