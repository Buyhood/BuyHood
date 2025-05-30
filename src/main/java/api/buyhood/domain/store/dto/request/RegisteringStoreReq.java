package api.buyhood.domain.store.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RegisteringStoreReq {

	@NotBlank(message = "가게 이름은 공백일 수 없습니다.")
	private final String storeName;

	@NotBlank(message = "주소는 공백일 수 없습니다.")
	private final String address;

	@NotNull
	private final Long sellerId;

	private final String description;
	private final LocalTime openedAt;
	private final LocalTime closedAt;

}
