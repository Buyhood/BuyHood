package api.buyhood.global.common.exception.enums;

import org.springframework.http.HttpStatus;

public interface ErrorCode {

	int getCode();

	String getMessage();

	HttpStatus getStatus();
}
