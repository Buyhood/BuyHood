package api.buyhood.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CategoryErrorCode implements ErrorCode {
	//에러코드 5000번대
	MAX_DEPTH_OVER(5101, "더 이상 하위 카테고리를 만들 수 없습니다.", HttpStatus.BAD_REQUEST),
	CATEGORY_NAME_SAME_AS_OLD(5102, "변경하려는 이름이 이전과 동일합니다.", HttpStatus.BAD_REQUEST),

	CATEGORY_NOT_FOUND(5141, "카테고리를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	CATEGORY_TYPE_NOT_FOUND(5142, "카테고리 타입을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

	DUPLICATE_CATEGORIES(5191, "중복된 카테고리 입니다.", HttpStatus.CONFLICT);

	private final int code;
	private final String message;
	private final HttpStatus status;
}
