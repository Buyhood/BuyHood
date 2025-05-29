package api.buyhood.domain.order.enums;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    LOCAL_QR,
    LOCAL_CARD,
    CREDIT_CARD,
    ZERO_PAY
}
