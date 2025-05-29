package api.buyhood.domain.order.service;

import api.buyhood.domain.cart.entity.Cart;
import api.buyhood.domain.cart.entity.CartItem;
import api.buyhood.domain.order.dto.response.OrderHistoryRes;
import api.buyhood.domain.order.entity.Order;
import api.buyhood.domain.order.entity.OrderHistory;
import api.buyhood.domain.order.repository.OrderHistoryRepository;
import api.buyhood.domain.order.repository.OrderRepository;
import api.buyhood.domain.product.entity.Product;
import api.buyhood.global.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static api.buyhood.global.common.exception.enums.OrderErrorCode.NOT_FOUND_ORDER;

@Service
@RequiredArgsConstructor
public class OrderHistoryService {

    private final OrderHistoryRepository orderHistoryRepository;
    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public List<OrderHistoryRes> findOrder(Long orderId) {

        List<OrderHistory> orderHistories = orderHistoryRepository.findAllByOrderId(orderId);

        if (orderHistories.isEmpty()) {
            throw new NotFoundException(NOT_FOUND_ORDER);
        }

        return orderHistories.stream()
                .map(orderHistory ->
                        OrderHistoryRes.of(
                                orderHistory.getOrder().getId(),
                                orderHistory.getProduct().getId(),
                                orderHistory.getQuantity(),
                                orderHistory.getCreatedAt()
                        )
                )
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<OrderHistoryRes> getOrdersByUser(int pageNum, int pageSize) {
        Long userId = 1L;

        Page<OrderHistory> orderHistories = orderHistoryRepository.findAllByUserId(userId, PageRequest.of(pageNum, pageSize));

        return orderHistories.map(orderHistory ->
                OrderHistoryRes.of(
                        orderHistory.getOrder().getId(),
                        orderHistory.getProduct().getId(),
                        orderHistory.getQuantity(),
                        orderHistory.getCreatedAt()
                )
        );
    }

    @Transactional(readOnly = true)
    public Page<OrderHistoryRes> getOrdersBySeller(int pageNum, int pageSize) {
        //todo: 로그인한 유저가 seller인지 확인
        Long storeId = 1L;

        Page<OrderHistory> orderHistories = orderHistoryRepository.findAllByStoreId(storeId, PageRequest.of(pageNum, pageSize));

        return orderHistories.map(orderHistory ->
                OrderHistoryRes.of(
                        orderHistory.getOrder().getId(),
                        orderHistory.getProduct().getId(),
                        orderHistory.getQuantity(),
                        orderHistory.getCreatedAt()
                )
        );
    }

    @Transactional(readOnly = true)
    public Page<OrderHistoryRes> getOrders(int pageNum, int pageSize) {
        //todo: 로그인한 유저가 관리자인지 확인
        Page<OrderHistory> orderHistories = orderHistoryRepository.findAll(PageRequest.of(pageNum, pageSize));

        return orderHistories.map(orderHistory ->
                OrderHistoryRes.of(
                        orderHistory.getOrder().getId(),
                        orderHistory.getProduct().getId(),
                        orderHistory.getQuantity(),
                        orderHistory.getCreatedAt()
                )
        );

    }

    @Transactional
    public void saveOrderHistory(Order order, Cart cart, Map<Long, Product> productMap) {

        for (CartItem item : cart.getCart()) {
            Product product = productMap.get(item.getProductId());
            OrderHistory orderHistory = OrderHistory.of(order, product, item.getQuantity());
            orderHistoryRepository.save(orderHistory);
        }
    }
}
