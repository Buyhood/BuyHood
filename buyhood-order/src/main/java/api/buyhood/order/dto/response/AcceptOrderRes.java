package api.buyhood.order.dto.response;

import api.buyhood.order.entity.Order;
import api.buyhood.order.enums.OrderStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalTime;

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
