package api.buyhood.order.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RejectOrderReq {
    @NotBlank(message = "거절 사유를 입력해주세요.")
    private final String message;
}
