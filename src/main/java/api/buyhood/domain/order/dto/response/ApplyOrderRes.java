package api.buyhood.domain.order.dto.response;

import api.buyhood.domain.cart.dto.response.CartRes;
import api.buyhood.domain.order.enums.OrderStatus;
import api.buyhood.domain.order.enums.PaymentMethod;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class ApplyOrderRes {

	private final Long storeId;
	private final CartRes orderInfo;
	private final long totalPrice;
	private final PaymentMethod paymentMethod;
	private final OrderStatus status;
	private final String requestMessage;
	private final LocalDateTime createAt;


	public static ApplyOrderRes of(Long storeId, CartRes orderInfo, long totalPrice, PaymentMethod paymentMethod,
		OrderStatus status, LocalDateTime createAt, String requestMessage) {
		return ApplyOrderRes.builder()
			.storeId(storeId)
			.orderInfo(orderInfo)
			.totalPrice(totalPrice)
			.paymentMethod(paymentMethod)
			.status(status)
			.createAt(createAt)
			.requestMessage(requestMessage)
			.build();
	}
}
