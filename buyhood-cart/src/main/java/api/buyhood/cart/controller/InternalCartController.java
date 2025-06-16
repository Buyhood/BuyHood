package api.buyhood.cart.controller;

import api.buyhood.cart.service.InternalCartService;
import api.buyhood.dto.cart.CartDto;
import api.buyhood.dto.cart.ExistsCartDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal")
public class InternalCartController {
    private final InternalCartService internalCartService;

    @GetMapping("/v1/carts/{userId}/exists")
    ExistsCartDto existsCart(@PathVariable Long userId) {
        return internalCartService.existsCart(userId);
    }

    @GetMapping("/v1/carts/{userId}")
    CartDto findCart(@PathVariable Long userId) {
        return internalCartService.findCart(userId);
    }

    @DeleteMapping("/v1/carts/{userId}")
    void clearCart(@PathVariable Long userId) {
        internalCartService.clearCart(userId);
    }
}
