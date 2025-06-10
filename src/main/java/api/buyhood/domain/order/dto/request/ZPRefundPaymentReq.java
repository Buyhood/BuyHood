package api.buyhood.domain.order.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ZPRefundPaymentReq {
    @NotNull(message = "merchantUid를 입력해주세요.")
    private final String merchantUid;

}
