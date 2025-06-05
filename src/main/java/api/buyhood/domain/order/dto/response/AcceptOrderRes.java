package api.buyhood.domain.order.dto.response;

import api.buyhood.domain.order.entity.Order;
import api.buyhood.domain.order.enums.OrderStatus;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class AcceptOrderRes {

	private final Long orderId;
	private final OrderStatus status;
	private final LocalTime readyAt;

	public static AcceptOrderRes of(Order order) {
		return AcceptOrderRes.builder()
			.orderId(order.getId())
			.readyAt(order.getReadyAt())
			.status(order.getStatus())
			.build();
	}

}
