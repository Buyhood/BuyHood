package api.buyhood.cart.service;

import api.buyhood.cart.client.ProductFeignClient;
import api.buyhood.cart.client.UserFeignClient;
import api.buyhood.cart.dto.request.CartReq;
import api.buyhood.cart.dto.request.CreateCartReq;
import api.buyhood.cart.dto.response.CartRes;
import api.buyhood.cart.entity.Cart;
import api.buyhood.cart.entity.CartItem;
import api.buyhood.cart.repository.CartRepository;
import api.buyhood.dto.product.request.GetProductReq;
import api.buyhood.dto.product.response.ProductFeignDto;
import api.buyhood.dto.user.UserFeignDto;
import api.buyhood.exception.InvalidRequestException;
import api.buyhood.exception.NotFoundException;
import api.buyhood.security.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static api.buyhood.errorcode.CartErrorCode.MULTIPLE_STORE_NOT_ALLOWED;
import static api.buyhood.errorcode.CartErrorCode.NOT_FOUND_CART;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductFeignClient productFeignClient;
    private final UserFeignClient userFeignClient;

    @Transactional
    public CartRes addItemsToCart(AuthUser authUser, CreateCartReq createCartReq) {
        UserFeignDto user = userFeignClient.getRoleUserOrElseThrow(authUser.getId());

        List<Long> productIdList = createCartReq.getCartItems().stream()
                .map(CartReq::getProductId)
                .distinct()
                .toList();

        // 조회된 상품의 ID 수가 요청된 상품의 ID 수보다 적다면 존재하지않는 상품이 있다는 의미(뭐가 없는진 모름)
        List<ProductFeignDto> products = productFeignClient.getProducts(new GetProductReq(productIdList));

        // 가게 중복 검증
        validateSingleStoreInCart(products);

        List<CartItem> cartItemList = createCartReq.getCartItems().stream()
                .map(item ->
                        CartItem.builder()
                                .productId(item.getProductId())
                                .quantity(item.getQuantity())
                                .build()
                )
                .toList();

        Cart cart = Cart.builder()
                .cart(cartItemList)
                .build();
        cartRepository.add(user.getId(), cart);

        return CartRes.of(cart);
    }

    @Transactional(readOnly = true)
    public CartRes findCart(AuthUser authUser) {
        UserFeignDto user = userFeignClient.getRoleUserOrElseThrow(authUser.getId());

        if (!cartRepository.existsCart(user.getId())) {
            throw new NotFoundException(NOT_FOUND_CART);
        }

        Cart cart = cartRepository.findCart(user.getId());

        return CartRes.of(cart);
    }

    @Transactional
    public void clearCart(AuthUser authUser) {
        UserFeignDto user = userFeignClient.getRoleUserOrElseThrow(authUser.getId());

        if (!cartRepository.existsCart(user.getId())) {
            throw new NotFoundException(NOT_FOUND_CART);
        }

        cartRepository.clearCart(user.getId());
    }

    private void validateSingleStoreInCart(List<ProductFeignDto> products) {
        long storeCount = products.stream()
                .map(ProductFeignDto::getStoreId)
                .distinct()
                .count();

        if (storeCount != 1) {
            throw new InvalidRequestException(MULTIPLE_STORE_NOT_ALLOWED);
        }
    }
}