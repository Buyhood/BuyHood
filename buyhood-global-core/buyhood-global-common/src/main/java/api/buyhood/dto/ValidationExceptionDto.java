package api.buyhood.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ValidationExceptionDto {

	private int code; // 에러 코드
	private String message; // 에러 메시지
	private List<FieldError> errors; // 필드별 에러 목록

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class FieldError {

		private String field;         // 필드명
		private Object rejectedValue; // 거부된 값
		private String reason;        // 에러 메시지
	}
}
