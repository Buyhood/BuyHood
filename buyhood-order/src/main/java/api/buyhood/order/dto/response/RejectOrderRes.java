package api.buyhood.order.dto.response;

import api.buyhood.order.entity.Order;
import api.buyhood.order.enums.OrderStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class RejectOrderRes {

	private final Long orderId;
	private final OrderStatus status;
	private final String message;

	public static RejectOrderRes of(Order order, String message) {
		return RejectOrderRes.builder()
			.orderId(order.getId())
			.status(order.getStatus())
			.message(message)
			.build();
	}
}
