package api.buyhood.domain.cart.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CartReq {
    @NotNull
    private final Long productId;

    @NotNull
    private final int quantity;
}
