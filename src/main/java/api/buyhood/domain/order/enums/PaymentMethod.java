package api.buyhood.domain.order.enums;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    CARD("card"),
    VBANK("vbank"),
    TRANS("trans"),
    PHONE("phone");

    private final String name;

    PaymentMethod(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
