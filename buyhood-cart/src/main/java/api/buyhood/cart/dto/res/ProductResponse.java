package api.buyhood.cart.dto.res;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductResponse {
    private Long id;
    private StoreResponse store;

    @Getter
    @NoArgsConstructor
    public static class StoreResponse {
        private Long id;
    }
}
