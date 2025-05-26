package api.buyhood.global.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public interface Response<T> {

	T getData();

	T getError();

	static <T> Response<T> ok(T data) {
		return new SuccessResponse<>(data);
	}

	static <T> Response<T> error(T error) {
		return new ErrorResponse<>(error);
	}
}
