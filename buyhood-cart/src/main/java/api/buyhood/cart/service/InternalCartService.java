package api.buyhood.cart.service;

import api.buyhood.cart.entity.Cart;
import api.buyhood.cart.repository.CartRepository;
import api.buyhood.dto.cart.CartDto;
import api.buyhood.dto.cart.CartItemDto;
import api.buyhood.dto.cart.ExistsCartDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InternalCartService {
    private final CartRepository cartRepository;

    @Transactional
    public ExistsCartDto existsCart(Long userId) {
        boolean exists = cartRepository.existsCart(userId);

        return new ExistsCartDto(exists);
    }

    @Transactional(readOnly = true)
    public CartDto findCart(Long userId) {

        Cart cart = cartRepository.findCart(userId);

        List<CartItemDto> cartList = cart.getCart().stream()
                .map(cartItem -> new CartItemDto(cartItem.getProductId(), cartItem.getQuantity()))
                .toList();

        return new CartDto(cartList);
    }

    @Transactional
    public void clearCart(Long userId) {
        cartRepository.clearCart(userId);
    }
}
