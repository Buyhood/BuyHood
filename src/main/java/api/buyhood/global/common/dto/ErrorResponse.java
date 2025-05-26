package api.buyhood.global.common.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ErrorResponse<T> implements Response<T> {

	private final T error;

	@Override
	public T getData() {
		return null;
	}
}
