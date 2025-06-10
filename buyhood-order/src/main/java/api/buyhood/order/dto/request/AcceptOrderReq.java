package api.buyhood.order.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalTime;

@Getter
@RequiredArgsConstructor
public class AcceptOrderReq {

	@NotNull(message = "예상 준비시간을 입력해주세요.")
	private final LocalTime readyAt;

}
