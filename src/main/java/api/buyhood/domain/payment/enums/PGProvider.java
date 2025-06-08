package api.buyhood.domain.payment.enums;

public enum PGProvider {
    TOSS("uplus"),
    NICE("nice"),
    KCP("kcp"),
    DANAL("danal"),
    KG("html5_inicis"),
    ZERO_PAY("zero_pay")
    ;

    private final String name;

    PGProvider(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
