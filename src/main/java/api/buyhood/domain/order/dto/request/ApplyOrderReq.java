package api.buyhood.domain.order.dto.request;

import api.buyhood.domain.order.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApplyOrderReq {

	@NotNull
	private final PaymentMethod paymentMethod;
	@NotNull
	private final Long storeId;
	private final String requestMessage;
}
