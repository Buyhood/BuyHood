package api.buyhood.domain.order.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApplyOrderReq {

	@NotNull(message = "가게 Id를 입력해주세요")
	private final Long storeId;
	private final String requestMessage;
}
