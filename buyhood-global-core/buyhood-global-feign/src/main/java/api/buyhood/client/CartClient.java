package api.buyhood.client;

import api.buyhood.dto.cart.CartDto;
import api.buyhood.dto.cart.ExistsCartDto;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface CartClient {

    @GetMapping("/internal/v1/carts/{userId}/exists")
    ExistsCartDto existsCart(@PathVariable Long userId);

    @GetMapping("/internal/v1/carts/{userId}")
    CartDto findCart(@PathVariable Long userId);

    @DeleteMapping("/internal/v1/carts/{userId}")
    void clearCart(@PathVariable Long userId);
}
