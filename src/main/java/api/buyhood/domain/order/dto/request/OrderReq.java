package api.buyhood.domain.order.dto.request;

import api.buyhood.domain.order.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class OrderReq {
    @NotNull
    private final PaymentMethod paymentMethod;

    @NotNull
    private final LocalDateTime pickupAt;
}
