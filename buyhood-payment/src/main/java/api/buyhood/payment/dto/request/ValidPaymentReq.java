package api.buyhood.payment.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ValidPaymentReq {

    @NotNull(message = "impUid를 입력해주세요.")
    private final String impUid;
}
