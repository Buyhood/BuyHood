package api.buyhood.domain.payment.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ZPayValidationReq {
    @NotNull(message = "merchantUid를 입력해주세요.")
    private final String merchantUid;

    @NotNull(message = "총 가격을 입력해주세요.")
    private final long totalPrice;

    @NotNull(message = "결제 상태를 입력해주세요.")
    private final String status;

}
