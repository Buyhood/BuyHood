package api.buyhood.handler;

import api.buyhood.dto.CustomExceptionDto;
import api.buyhood.dto.Response;
import api.buyhood.dto.ValidationExceptionDto;
import api.buyhood.errorcode.ErrorCode;
import api.buyhood.errorcode.ServerErrorCode;
import api.buyhood.exception.BaseException;
import api.buyhood.exception.FilterAuthenticationException;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	//커스텀 예외 처리 : BaseException을 상속받은 예외는 모두 이 핸들러로 처리
	@ExceptionHandler(value = BaseException.class)
	public Response<CustomExceptionDto> baseException(
		BaseException be,
		HttpServletResponse response
	) {
		log.error("[{}] cause: ", be.getClass().getSimpleName(), be);
		ErrorCode errorCode = be.getErrorCode();
		response.setStatus(errorCode.getStatus().value());
		return Response.error(new CustomExceptionDto(errorCode.getCode(), errorCode.getMessage()));
	}

	// 서버 내부 에러 처리: IOException 등 예상치 못한 시스템 오류 발생 시
	@ExceptionHandler(value = IOException.class)
	public Response<CustomExceptionDto> ioExceptionHandler(
		IOException ie,
		HttpServletResponse response
	) {
		log.error("[{}] cause: ", ie.getClass().getSimpleName(), ie);
		ErrorCode errorCode = ServerErrorCode.INTERNAL_SERVER_ERROR;
		response.setStatus(errorCode.getStatus().value());
		return Response.error(new CustomExceptionDto(errorCode.getCode(), errorCode.getMessage()));
	}

	// 유효성 검사 실패 처리: @Valid로 검증 실패 시 발생하는 예외 처리
	@ExceptionHandler(value = MethodArgumentNotValidException.class)
	public Response<ValidationExceptionDto> methodArgumentNotValidExceptionHandler(
		MethodArgumentNotValidException me,
		HttpServletResponse response
	) {
		log.error("[{}] cause: ", me.getClass().getSimpleName(), me);
		ErrorCode errorCode = ServerErrorCode.INVALID_INPUT_VALUE;
		response.setStatus(errorCode.getStatus().value());

		List<FieldError> fieldErrors = me.getBindingResult().getFieldErrors();
		List<ValidationExceptionDto.FieldError> errors = fieldErrors.stream()
			.map(fieldError -> new ValidationExceptionDto.FieldError(
				fieldError.getField(),
				fieldError.getRejectedValue(),
				fieldError.getDefaultMessage()
			))
			.collect(Collectors.toList());

		return Response.error(new ValidationExceptionDto(
			errorCode.getCode(),
			errorCode.getMessage(),
			errors
		));
	}

	// 파라미터 타입 불일치 처리: 잘못된 타입의 파라미터가 전달될 때 발생하는 예외 처리
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public Response<CustomExceptionDto> methodArgumentTypeMismatchExceptionHandler(
		MethodArgumentTypeMismatchException e,
		HttpServletResponse response
	) {
		log.error("[{}] cause: ", e.getClass().getSimpleName(), e);
		ErrorCode errorCode = ServerErrorCode.INVALID_INPUT_PARAM;
		response.setStatus(errorCode.getStatus().value());
		return Response.error(new CustomExceptionDto(errorCode.getCode(), errorCode.getMessage()));
	}

	// 데이터베이스 접근 오류 처리: DB 관련 예외 발생 시
	@ExceptionHandler(DataAccessException.class)
	public Response<CustomExceptionDto> dataAccessExceptionHandler(
		DataAccessException e,
		HttpServletResponse response
	) {
		log.error("[{}] cause: ", e.getClass().getSimpleName(), e);
		ErrorCode errorCode = ServerErrorCode.DATABASE_ACCESS_ERROR;
		response.setStatus(errorCode.getStatus().value());
		return Response.error(new CustomExceptionDto(errorCode.getCode(), errorCode.getMessage()));
	}

	// 존재하지 않는 API 요청 처리
	@ExceptionHandler(NoHandlerFoundException.class)
	public Response<CustomExceptionDto> noHandlerFoundExceptionHandler(
		NoHandlerFoundException e,
		HttpServletResponse response
	) {
		log.error("[{}] cause: ", e.getClass().getSimpleName(), e);
		ErrorCode errorCode = ServerErrorCode.NOT_FOUND;
		response.setStatus(errorCode.getStatus().value());
		return Response.error(new CustomExceptionDto(errorCode.getCode(), errorCode.getMessage()));
	}

	//예상치 못한 모든 예외 처리
	@ExceptionHandler(Exception.class)
	public Response<CustomExceptionDto> exceptionHandler(
		Exception e,
		HttpServletResponse response
	) {
		log.error("[{}] cause: ", e.getClass().getSimpleName(), e);
		ErrorCode errorCode = ServerErrorCode.INTERNAL_SERVER_ERROR;
		response.setStatus(errorCode.getStatus().value());
		return Response.error(new CustomExceptionDto(errorCode.getCode(), errorCode.getMessage()));
	}

	// 핸들러에 예외 처리 추가
	@ExceptionHandler(FilterAuthenticationException.class)
	public Response<CustomExceptionDto> handleFilterAuthException(
		FilterAuthenticationException e,
		HttpServletResponse response
	) {
		response.setStatus(e.getErrorCode().getStatus().value());
		return Response.error(
			new CustomExceptionDto(
				e.getErrorCode().getCode(),
				e.getErrorCode().getMessage()
			)
		);
	}
}
