package api.buyhood.payment.dto.request;

import api.buyhood.payment.enums.PGProvider;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PaymentReq {
    @NotNull(message = "PGProvider를 입력해주세요.")
    private final PGProvider pg;
}
