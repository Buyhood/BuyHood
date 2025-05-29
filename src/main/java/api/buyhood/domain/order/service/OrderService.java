package api.buyhood.domain.order.service;

import api.buyhood.domain.cart.dto.response.CartRes;
import api.buyhood.domain.cart.entity.Cart;
import api.buyhood.domain.cart.entity.CartItem;
import api.buyhood.domain.cart.repository.CartRepository;
import api.buyhood.domain.order.dto.request.CreateOrderReq;
import api.buyhood.domain.order.dto.response.CreateOrderRes;
import api.buyhood.domain.order.entity.Order;
import api.buyhood.domain.order.repository.OrderRepository;
import api.buyhood.domain.product.entity.Product;
import api.buyhood.domain.product.repository.ProductRepository;
import api.buyhood.domain.product.service.ProductService;
import api.buyhood.domain.store.entity.Store;
import api.buyhood.domain.store.repository.StoreRepository;
import api.buyhood.domain.user.entity.User;
import api.buyhood.domain.user.repository.UserRepository;
import api.buyhood.global.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static api.buyhood.global.common.exception.enums.CartErrorCode.NOT_FOUND_CART;
import static api.buyhood.global.common.exception.enums.StoreErrorCode.NOT_FOUND_STORE;
import static api.buyhood.global.common.exception.enums.UserErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderHistoryService orderHistoryService;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    @Transactional
    public CreateOrderRes createOrder(CreateOrderReq createOrderReq)  {

        Long userId = 1L;

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

        Store store = storeRepository.findById(createOrderReq.getStoreId())
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_STORE));

        if (!cartRepository.existsCart(userId)) {
            throw new NotFoundException(NOT_FOUND_CART);
        }

        Cart cart = cartRepository.findCart(userId);

        List<Long> productIdList = cart.getCart().stream()
                .map(CartItem::getProductId)
                .toList();

        Map<Long, Product> productMap = productRepository.findAllById(productIdList).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        Order order = Order.of(store, user, createOrderReq.getPaymentMethod(), getTotalPrice(productMap, cart.getCart()), createOrderReq.getPickupAt());
        orderRepository.save(order);
        orderHistoryService.saveOrderHistory(order, cart, productMap);

        productService.decreaseStock(cart, productMap);
        cartRepository.clearCart(userId);

        return CreateOrderRes.of(order.getStore().getId(), CartRes.of(cart),order.getTotalPrice(), order.getPaymentMethod(), order.getStatus(), order.getPickupAt(), order.getCreatedAt());
    }

    private long getTotalPrice(Map<Long, Product> productMap,List<CartItem> cartItemList) {
        long totalPrice = 0L;

        for (CartItem item : cartItemList) {
            Product product = productMap.get(item.getProductId());
            totalPrice += product.getPrice() * item.getQuantity();
        }

        return totalPrice;
    }
}
