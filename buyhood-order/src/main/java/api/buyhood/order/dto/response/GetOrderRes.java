package api.buyhood.order.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@RequiredArgsConstructor
public class GetOrderRes {

	private final Long orderId;
	private final Long productId;
	private final int quantity;
	private final LocalDateTime createdAt;


	public static GetOrderRes of(Long orderId, Long productId, int quantity, LocalDateTime createdAt) {
		return GetOrderRes.builder()
			.orderId(orderId)
			.productId(productId)
			.quantity(quantity)
			.createdAt(createdAt)
			.build();
	}
}
