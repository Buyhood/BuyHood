package api.buyhood.domain.order.service;

import api.buyhood.domain.cart.entity.Cart;
import api.buyhood.domain.cart.entity.CartItem;
import api.buyhood.domain.order.entity.Order;
import api.buyhood.domain.order.entity.OrderHistory;
import api.buyhood.domain.order.repository.OrderHistoryRepoditory;
import api.buyhood.domain.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderHistoryService {

    private final OrderHistoryRepoditory orderHistoryRepoditory;

    @Transactional
    public void saveOrderHistory(Order order, Cart cart, Map<Long, Product> productMap) {

        for (CartItem item : cart.getCart()) {
            Product product = productMap.get(item.getProductId());
            OrderHistory orderHistory = OrderHistory.of(order, product, item.getQuantity());
            orderHistoryRepoditory.save(orderHistory);
        }
    }
}
