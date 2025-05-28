package api.buyhood.domain.order.service;

import api.buyhood.domain.cart.dto.response.CartRes;
import api.buyhood.domain.cart.entity.Cart;
import api.buyhood.domain.cart.entity.CartItem;
import api.buyhood.domain.cart.repository.CartRepository;
import api.buyhood.domain.order.dto.request.OrderReq;
import api.buyhood.domain.order.dto.response.OrderRes;
import api.buyhood.domain.order.dto.response.CreateOrderRes;
import api.buyhood.domain.order.entity.Order;
import api.buyhood.domain.order.repository.OrderRepository;
import api.buyhood.domain.product.entity.Product;
import api.buyhood.domain.product.repository.ProductRepository;
import api.buyhood.global.common.exception.InvalidRequestException;
import api.buyhood.global.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static api.buyhood.global.common.exception.enums.CartErrorCode.NOT_FOUND_CART;
import static api.buyhood.global.common.exception.enums.OrderErrorCode.NOT_FOUND_ORDER;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    @Transactional
    public CreateOrderRes createOrder(OrderReq orderReq) {

        Long userId = 1L;

        if (!cartRepository.existsCart(userId)) {
            throw  new InvalidRequestException(NOT_FOUND_CART);
        }

        Cart cart = cartRepository.findCart(userId);

        List<Long> productIdList = cart.getCart().stream()
                .map(CartItem::getProductId)
                .toList();

        Map<Long, Product> productMap = productRepository.findAllById(productIdList).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        Order order = Order.of(orderReq.getPaymentMethod(), getTotalPrice(productMap, cart.getCart()), orderReq.getPickupAt());
        orderRepository.save(order);
        cartRepository.clearCart(userId);

        for (CartItem item : cart.getCart()) {
            Product product = productMap.get(item.getProductId());
            product.decreaseStock(item.getQuantity());
        }

        return CreateOrderRes.of(CartRes.of(cart),order.getTotalPrice(), order.getPaymentMethod(), order.getStatus(), order.getPickupAt());
    }

    @Transactional(readOnly = true)
    public OrderRes findOrder(Long orderId) {

        Order order = orderRepository.findNotDeletedById(orderId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_ORDER));

        return OrderRes.of(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderRes> findOrders(int pageNum, int pageSize) {
        Page<Order> orderList = orderRepository.findNotDeletedAll(PageRequest.of(pageNum, pageSize));

        return orderList.map(OrderRes::of);
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
