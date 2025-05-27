package api.buyhood.global.common.handler;

import api.buyhood.global.common.dto.CustomExceptionDto;
import api.buyhood.global.common.dto.Response;
import api.buyhood.global.common.exception.InvalidRequestException;
import api.buyhood.global.common.exception.enums.ErrorCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(value = InvalidRequestException.class)
	public Response<CustomExceptionDto> invalidRequestException(
		InvalidRequestException ire,
		HttpServletResponse response
	) {
		log.error("[InvalidRequestException] cause: ", ire);
		ErrorCode errorCode = ire.getErrorCode();
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		return Response.error(new CustomExceptionDto(errorCode.getCode(), errorCode.getMessage()));
	}
}
