package api.buyhood.domain.order.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@Getter
@RequiredArgsConstructor
public class OrderHistoryRes {

	private final Long orderId;
	private final Long productId;
	private final int quantity;
	private final LocalDateTime createdAt;


	public static OrderHistoryRes of(Long orderId, Long productId, int quantity, LocalDateTime createdAt) {
		return OrderHistoryRes.builder()
			.orderId(orderId)
			.productId(productId)
			.quantity(quantity)
			.createdAt(createdAt)
			.build();
	}
}
