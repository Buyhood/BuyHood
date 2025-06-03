package payment.dto.request;

import api.buyhood.domain.order.enums.PaymentMethod;
import payment.enums.PGProvider;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PaymentReq {
    @NotNull(message = "PGProvider를 입력해주세요.")
    private final PGProvider pg;

    @NotNull(message = "결제 방식을 입력해주세요.")
    private final PaymentMethod paymentMethod;
}
