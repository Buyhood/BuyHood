package api.buyhood.domain.payment.enums;

public enum PGProvider {
    KAKAOPAY("kakaopay"),
    TOSS("uplus"),
    NAVERPAY("naverpay"),
    NICE("nice"),
    KCP("kcp"),
    DANAL("danal"),
    UPLUS("uplus"),
    KG("html5_inicis"),
    ZERO_PAY("zeropay");

    private final String name;

    PGProvider(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
