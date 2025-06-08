package api.buyhood.store.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RegisterStoreReq {

	@NotBlank(message = "가게 이름은 공백일 수 없습니다.")
	private final String storeName;

	@NotBlank(message = "주소는 공백일 수 없습니다.")
	private final String address;

	@NotNull(message = "카테고리를 지정해야 합니다.")
	private final Long categoryId;

	@NotNull(message = "배달 가능 여부는 공백일 수 없습니다.")
	private final Boolean isDeliverable;

	private final String description;
	private final String openedAt;
	private final String closedAt;

}
