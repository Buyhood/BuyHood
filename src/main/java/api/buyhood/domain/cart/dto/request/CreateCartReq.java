package api.buyhood.domain.cart.dto.request;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CreateCartReq {
    @Valid
    private List<CartReq> cartItems;
}
