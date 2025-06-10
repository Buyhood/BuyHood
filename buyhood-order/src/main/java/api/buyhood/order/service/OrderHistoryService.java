package api.buyhood.order.service;

import api.buyhood.cart.entity.Cart;
import api.buyhood.cart.entity.CartItem;
import api.buyhood.dto.store.StoreFeignDto;
import api.buyhood.dto.user.UserFeignDto;
import api.buyhood.exception.ForbiddenException;
import api.buyhood.exception.NotFoundException;
import api.buyhood.order.client.StoreFeignClient;
import api.buyhood.order.client.UserFeignClient;
import api.buyhood.order.dto.response.GetOrderRes;
import api.buyhood.order.entity.Order;
import api.buyhood.order.entity.OrderHistory;
import api.buyhood.order.repository.OrderHistoryRepository;
import api.buyhood.product.entity.Product;
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
import static api.buyhood.errorcode.UserErrorCode.ROLE_MISMATCH;

@Service
@RequiredArgsConstructor
public class OrderHistoryService {

	private final OrderHistoryRepository orderHistoryRepository;
	private final UserFeignClient userFeignClient;
	private final StoreFeignClient storeFeignClient;

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
					orderHistory.getProductId(),
					orderHistory.getQuantity(),
					orderHistory.getCreatedAt()
				)
			)
			.toList();
	}

	@Transactional(readOnly = true)
	public Page<GetOrderRes> getOrdersByUser(int pageNum, int pageSize, Long userId) {
		UserFeignDto user = userFeignClient.getRoleUserOrElseThrow(userId);

		Page<OrderHistory> orderHistories = orderHistoryRepository.findAllByUserId(user.getUserId(),
			PageRequest.of(pageNum, pageSize));

		return orderHistories.map(orderHistory ->
			GetOrderRes.of(
				orderHistory.getOrder().getId(),
				orderHistory.getProductId(),
				orderHistory.getQuantity(),
				orderHistory.getCreatedAt()
			)
		);
	}

	@Transactional(readOnly = true)
	public Page<GetOrderRes> getOrdersBySeller(int pageNum, int pageSize, Long storeId, Long userId) {
		StoreFeignDto store = storeFeignClient.getStoreOrElseThrow(storeId);

		UserFeignDto user = userFeignClient.getRoleSellerOrElseThrow(userId);

		if (!SELLER.equals(user.getRole())) {
			throw new ForbiddenException(ROLE_MISMATCH);
		}

		if (!store.getSellerId().equals(user.getUserId())) {
			throw new ForbiddenException(NOT_OWNER_OF_STORE);
		}

		Page<OrderHistory> orderHistories = orderHistoryRepository.findAllByStoreId(store.getStoreId(),
			PageRequest.of(pageNum, pageSize));

		return orderHistories.map(orderHistory ->
			GetOrderRes.of(
				orderHistory.getOrder().getId(),
				orderHistory.getProductId(),
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
				orderHistory.getProductId(),
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
				.productId(product.getId())
				.quantity(item.getQuantity())
				.build();

			orderHistoryRepository.save(orderHistory);
		}
	}
}
