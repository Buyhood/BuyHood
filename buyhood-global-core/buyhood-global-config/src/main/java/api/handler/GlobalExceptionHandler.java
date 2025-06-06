package api.handler;

import api.dto.CustomExceptionDto;
import api.dto.Response;
import api.errorcode.ErrorCode;
import api.exception.BaseException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(value = BaseException.class)
	public Response<CustomExceptionDto> invalidRequestException(
		BaseException be,
		HttpServletResponse response
	) {
		log.error("[{}] cause: ", be.getClass().getSimpleName(), be);
		ErrorCode errorCode = be.getErrorCode();
		response.setStatus(errorCode.getStatus().value());
		return Response.error(new CustomExceptionDto(errorCode.getCode(), errorCode.getMessage()));
	}
}
