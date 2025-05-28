package api.buyhood.domain.cart.service;

import api.buyhood.domain.cart.dto.request.CreateCartReq;
import api.buyhood.domain.cart.dto.response.CartItemRes;
import api.buyhood.domain.cart.dto.response.CartRes;
import api.buyhood.domain.cart.entity.Cart;
import api.buyhood.domain.cart.entity.CartItem;
import api.buyhood.domain.cart.repository.CartRepository;
import api.buyhood.global.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static api.buyhood.global.common.exception.enums.CartErrorCode.NOT_FOUND_CART;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    @Transactional
    public CartRes addItemsToCart(CreateCartReq createCartReq) {

        //todo: userId 수정
        Long userId = 1L;

        //todo: product 존재 여부 확인 추가하기
        List<CartItem> cartItemList = createCartReq.getCartItems().stream()
                .map(item ->
                        CartItem.of(item.getProductId(), item.getQuantity())
                )
                .toList();

        Cart cart = Cart.of(cartItemList);
        cartRepository.add(userId, cart);

        return getCartRes(cart);
    }

    @Transactional(readOnly = true)
    public CartRes findCart() {
        Long userId = 1L;

        if (!cartRepository.existsCart(userId)) {
            throw new NotFoundException(NOT_FOUND_CART);
        }

        Cart cart = cartRepository.findCart(userId);

        return getCartRes(cart);
    }

    @Transactional
    public void clearCart() {
        Long userId = 1L;

        if (!cartRepository.existsCart(userId)) {
            throw new NotFoundException(NOT_FOUND_CART);
        }

        cartRepository.clearCart(userId);
    }

    //dto 변환
    private CartRes getCartRes(Cart cart) {
        List<CartItemRes> cartList = cart.getCart().stream()
                .map(cartItem ->
                        CartItemRes.of(cartItem.getProductId(), cartItem.getQuantity())
                ).toList();

        return CartRes.of(cartList);
    }
}
