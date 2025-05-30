package api.buyhood.domain.order.dto.request;

import java.time.LocalTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AcceptOrderReq {

	private final LocalTime readyAt;

}
