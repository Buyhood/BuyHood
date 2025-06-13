package api.buyhood.order.enums;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    CARD,
    VBANK,
    TRANS,
    ZERO_PAY,
    PHONE;
}
