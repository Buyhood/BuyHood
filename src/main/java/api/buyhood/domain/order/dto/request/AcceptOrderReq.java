package api.buyhood.domain.order.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AcceptOrderReq {

	@NotNull(message = "예상 준비시간을 입력해주세요.")
	private final LocalTime readyAt;

}
