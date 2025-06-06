package api.buyhood.domain.order.enums;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    CARD,
    VBANK,
    TRANS,
    ZERO_PAY,
    PHONE;
}
