package api.buyhood.domain.order.dto.response;

import api.buyhood.domain.order.entity.Order;
import api.buyhood.domain.order.enums.OrderStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class RejectOrderRes {

	private final Long orderId;
	private final OrderStatus status;

	public static RejectOrderRes of(Order order) {
		return RejectOrderRes.builder()
			.orderId(order.getId())
			.status(order.getStatus())
			.build();
	}
}
