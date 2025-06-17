package api.buyhood.payment.enums;

/**
 * 결제 대행사(PG: Payment Gateway) 제공자 enum
 *
 * - 제로페이 (직접 결제 수단)
 * - KG이니시스 (등록됨 - 사용 가능)
 * - 토스페이먼츠 (등록됨 - 사용 가능)
 * - NICE페이 (미등록 - 추후 등록 예정)
 * - KCP (미등록 - 추후 등록 예정)
 * - 다날 (미등록 - 추후 등록 예정)
 */
public enum PGProvider {
    ZERO_PAY("zero_pay"),
    KG("html5_inicis"),
    TOSS("uplus"),
    NICE("nice"),
    KCP("kcp"),
    DANAL("danal");


    private final String name;

    PGProvider(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
