package api.buyhood.order.service;

import api.buyhood.cart.entity.Cart;
import api.buyhood.cart.entity.CartItem;
import api.buyhood.domain.user.entity.User;
import api.buyhood.domain.user.repository.UserRepository;
import api.buyhood.exception.ForbiddenException;
import api.buyhood.exception.NotFoundException;
import api.buyhood.order.dto.response.GetOrderRes;
import api.buyhood.order.entity.Order;
import api.buyhood.order.entity.OrderHistory;
import api.buyhood.order.repository.OrderHistoryRepository;
import api.buyhood.product.entity.Product;
import api.buyhood.store.entity.Store;
import api.buyhood.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static api.buyhood.enums.UserRole.SELLER;
import static api.buyhood.errorcode.OrderErrorCode.NOT_FOUND_ORDER;
import static api.buyhood.errorcode.OrderErrorCode.NOT_OWNER_OF_STORE;
import static api.buyhood.errorcode.StoreErrorCode.STORE_NOT_FOUND;
import static api.buyhood.errorcode.UserErrorCode.ROLE_MISMATCH;
import static api.buyhood.errorcode.UserErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class OrderHistoryService {

	private final OrderHistoryRepository orderHistoryRepository;
	private final UserRepository userRepository;
	private final StoreRepository storeRepository;

	@Transactional(readOnly = true)
	public List<GetOrderRes> findOrder(Long orderId) {

		List<OrderHistory> orderHistories = orderHistoryRepository.findAllByOrderId(orderId);

		if (orderHistories.isEmpty()) {
			throw new NotFoundException(NOT_FOUND_ORDER);
		}

		return orderHistories.stream()
			.map(orderHistory ->
				GetOrderRes.of(
					orderHistory.getOrder().getId(),
					orderHistory.getProduct().getId(),
					orderHistory.getQuantity(),
					orderHistory.getCreatedAt()
				)
			)
			.toList();
	}

	@Transactional(readOnly = true)
	public Page<GetOrderRes> getOrdersByUser(int pageNum, int pageSize, Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundException(USER_NOT_FOUND)
			);

		Page<OrderHistory> orderHistories = orderHistoryRepository.findAllByUserId(user.getId(),
			PageRequest.of(pageNum, pageSize));

		return orderHistories.map(orderHistory ->
			GetOrderRes.of(
				orderHistory.getOrder().getId(),
				orderHistory.getProduct().getId(),
				orderHistory.getQuantity(),
				orderHistory.getCreatedAt()
			)
		);
	}

	@Transactional(readOnly = true)
	public Page<GetOrderRes> getOrdersBySeller(int pageNum, int pageSize, Long storeId, Long userId) {
		Store store = storeRepository.findById(storeId)
			.orElseThrow(() -> new NotFoundException(STORE_NOT_FOUND));

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

		if (!SELLER.equals(user.getRole())) {
			throw new ForbiddenException(ROLE_MISMATCH);
		}

		if (!store.getSellerId().equals(user.getId())) {
			throw new ForbiddenException(NOT_OWNER_OF_STORE);
		}

		Page<OrderHistory> orderHistories = orderHistoryRepository.findAllByStoreId(store.getId(),
			PageRequest.of(pageNum, pageSize));

		return orderHistories.map(orderHistory ->
			GetOrderRes.of(
				orderHistory.getOrder().getId(),
				orderHistory.getProduct().getId(),
				orderHistory.getQuantity(),
				orderHistory.getCreatedAt()
			)
		);
	}

	@Transactional(readOnly = true)
	public Page<GetOrderRes> getOrders(int pageNum, int pageSize) {
		//todo: 로그인한 유저가 관리자인지 확인
		Page<OrderHistory> orderHistories = orderHistoryRepository.findAll(PageRequest.of(pageNum, pageSize));

		return orderHistories.map(orderHistory ->
			GetOrderRes.of(
				orderHistory.getOrder().getId(),
				orderHistory.getProduct().getId(),
				orderHistory.getQuantity(),
				orderHistory.getCreatedAt()
			)
		);

	}

	public void saveOrderHistory(Order order, Cart cart, Map<Long, Product> productMap) {

		for (CartItem item : cart.getCart()) {
			Product product = productMap.get(item.getProductId());
			OrderHistory orderHistory = OrderHistory.builder()
				.order(order)
				.product(product)
				.quantity(item.getQuantity())
				.build();

			orderHistoryRepository.save(orderHistory);
		}
	}
}
