package api.buyhood.order.service;

import api.buyhood.dto.cart.CartDto;
import api.buyhood.dto.cart.CartItemDto;
import api.buyhood.dto.product.response.ProductFeignDto;
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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static api.buyhood.errorcode.OrderErrorCode.NOT_FOUND_ORDER;
import static api.buyhood.errorcode.OrderErrorCode.NOT_OWNER_OF_STORE;

@Service
@RequiredArgsConstructor
public class OrderHistoryService {

	private final OrderHistoryRepository orderHistoryRepository;
	private final UserFeignClient userFeignClient;
	private final StoreFeignClient storeFeignClient;

	@Transactional(readOnly = true)
	public List<GetOrderRes> findOrder(Long orderId, Long userId) {

		List<OrderHistory> orderHistories = orderHistoryRepository.findAllByOrderIdAndUserId(orderId, userId);

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

		Page<OrderHistory> orderHistories = orderHistoryRepository.findAllByUserId(user.getId(),
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

		if (!store.getSellerId().equals(user.getId())) {
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
	public Page<GetOrderRes> getOrders(int pageNum, int pageSize, Long userId) {

		userFeignClient.getRoleAdminOrElseThrow(userId);
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

	public void saveOrderHistory(Order order, CartDto cart, Map<Long, ProductFeignDto> productMap) {

		for (CartItemDto item : cart.getCartList()) {
			ProductFeignDto product = productMap.get(item.getProductId());
			OrderHistory orderHistory = OrderHistory.builder()
				.order(order)
				.productId(product.getProductId())
				.quantity(item.getQuantity())
				.build();

			orderHistoryRepository.save(orderHistory);
		}
	}
}
