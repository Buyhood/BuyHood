package api.errorcode;

import org.springframework.http.HttpStatus;

public interface ErrorCode {

	int getCode();

	String getMessage();

	HttpStatus getStatus();
}
