package api.buyhood.order.dto.response;

import api.buyhood.cart.dto.response.CartRes;
import api.buyhood.order.enums.OrderStatus;
import api.buyhood.order.enums.PaymentMethod;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@RequiredArgsConstructor
public class ApplyOrderRes {

	private final Long storeId;
	private final CartRes orderInfo;
	private final PaymentMethod paymentMethod;
	private final BigDecimal totalPrice;
	private final OrderStatus status;
	private final String requestMessage;
	private final LocalDateTime createAt;


	public static ApplyOrderRes of(Long storeId, CartRes orderInfo, PaymentMethod paymentMethod, BigDecimal totalPrice,
								   OrderStatus status, LocalDateTime createAt, String requestMessage) {
		return ApplyOrderRes.builder()
			.storeId(storeId)
			.orderInfo(orderInfo)
			.paymentMethod(paymentMethod)
			.totalPrice(totalPrice)
			.status(status)
			.createAt(createAt)
			.requestMessage(requestMessage)
			.build();
	}
}
