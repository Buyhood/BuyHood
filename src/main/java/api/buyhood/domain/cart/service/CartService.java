package api.buyhood.domain.cart.service;

import api.buyhood.domain.cart.dto.request.CreateCartReq;
import api.buyhood.domain.cart.dto.response.CartRes;
import api.buyhood.domain.cart.entity.CartItem;
import api.buyhood.domain.cart.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    @Transactional
    public CartRes addItemsToCart(CreateCartReq createCartReq) {

        //todo: userId 수정
        Long userId = 1L;

        //todo: product 존재 여부 확인 추가하기
        List<CartItem> cartList = createCartReq.getCartItems().stream()
                .map(item ->
                        CartItem.builder()
                                .productId(item.getProductId())
                                .quantity(item.getQuantity())
                                .build()
                )
                .toList();

        cartRepository.add(userId, cartList);

        return CartRes.builder()
                .cartList(createCartReq)
                .build();
    }
}
