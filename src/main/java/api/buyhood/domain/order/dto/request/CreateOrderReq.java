package api.buyhood.domain.order.dto.request;

import api.buyhood.domain.order.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class CreateOrderReq {
    @NotNull
    private final PaymentMethod paymentMethod;

    @NotNull
    private final LocalDateTime pickupAt;

    @NotNull
    private final Long storeId;
}
