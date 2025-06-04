package api.buyhood.domain.order.service;

import api.buyhood.domain.auth.entity.AuthUser;
import api.buyhood.domain.cart.dto.response.CartRes;
import api.buyhood.domain.cart.entity.Cart;
import api.buyhood.domain.cart.entity.CartItem;
import api.buyhood.domain.cart.repository.CartRepository;
import api.buyhood.domain.order.dto.request.AcceptOrderReq;
import api.buyhood.domain.order.dto.request.ApplyOrderReq;
import api.buyhood.domain.order.dto.request.RefundPaymentReq;
import api.buyhood.domain.order.dto.request.ZPRefundPaymentReq;
import api.buyhood.domain.order.dto.response.AcceptOrderRes;
import api.buyhood.domain.order.dto.response.ApplyOrderRes;
import api.buyhood.domain.order.dto.response.RejectOrderRes;
import api.buyhood.domain.order.entity.Order;
import api.buyhood.domain.order.entity.OrderHistory;
import api.buyhood.domain.order.enums.OrderStatus;
import api.buyhood.domain.order.repository.OrderHistoryRepository;
import api.buyhood.domain.order.repository.OrderRepository;
import api.buyhood.domain.payment.entity.Payment;
import api.buyhood.domain.payment.enums.PayStatus;
import api.buyhood.domain.payment.repository.PaymentRepository;
import api.buyhood.domain.product.entity.Product;
import api.buyhood.domain.product.repository.ProductRepository;
import api.buyhood.domain.product.service.ProductService;
import api.buyhood.domain.seller.entity.Seller;
import api.buyhood.domain.seller.repository.SellerRepository;
import api.buyhood.domain.store.entity.Store;
import api.buyhood.domain.store.repository.StoreRepository;
import api.buyhood.domain.user.entity.User;
import api.buyhood.domain.user.repository.UserRepository;
import api.buyhood.global.common.exception.ForbiddenException;
import api.buyhood.global.common.exception.InvalidRequestException;
import api.buyhood.global.common.exception.NotFoundException;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static api.buyhood.domain.order.enums.OrderStatus.ACCEPTED;
import static api.buyhood.global.common.exception.enums.CartErrorCode.NOT_FOUND_CART;
import static api.buyhood.global.common.exception.enums.OrderErrorCode.*;
import static api.buyhood.global.common.exception.enums.PaymentErrorCode.*;
import static api.buyhood.global.common.exception.enums.ProductErrorCode.PRODUCT_NOT_FOUND;
import static api.buyhood.global.common.exception.enums.SellerErrorCode.SELLER_NOT_FOUND;
import static api.buyhood.global.common.exception.enums.StoreErrorCode.STORE_NOT_FOUND;
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
	private final SellerRepository sellerRepository;
	private final PaymentRepository paymentRepository;
	private final IamportClient iamportClient;
	private final OrderHistoryRepository orderHistoryRepository;

	//주문 요청
	@Transactional
	public ApplyOrderRes applyOrder(ApplyOrderReq req, AuthUser authUser) {

		Store store = storeRepository.findById(req.getStoreId())
			.orElseThrow(() -> new NotFoundException(STORE_NOT_FOUND));

		User user = userRepository.findById(authUser.getId())
			.orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

		if (!cartRepository.existsCart(user.getId())) {
			throw new NotFoundException(NOT_FOUND_CART);
		}

		Cart cart = cartRepository.findCart(user.getId());

		List<Long> productIdList = cart.getCart().stream()
			.map(CartItem::getProductId)
			.toList();

		Map<Long, Product> productMap = productRepository.findAllById(productIdList).stream()
			.collect(Collectors.toMap(Product::getId, p -> p));

		String orderName = creatOrderName(productMap);

		Order order = Order.builder()
			.store(store)
			.user(user)
			.name(orderName)
			.paymentMethod(req.getPaymentMethod())
			.requestMessage(req.getRequestMessage())
			.status(OrderStatus.PENDING)
			.totalPrice(getTotalPrice(productMap, cart.getCart()))
			.build();
		orderRepository.save(order);
		orderHistoryService.saveOrderHistory(order, cart, productMap);
		cartRepository.clearCart(user.getId());

		productService.decreaseStock(cart, productMap);

		return ApplyOrderRes.of(order.getStore().getId(), CartRes.of(cart),order.getPaymentMethod(), order.getTotalPrice(), order.getStatus(), order.getCreatedAt(), order.getRequestMessage());
	}

	//주문 승인

	@Transactional
	public AcceptOrderRes acceptOrder(AcceptOrderReq req, Long orderId, AuthUser authUser) {

		Seller seller = sellerRepository.findById(authUser.getId())
			.orElseThrow(() -> new NotFoundException(SELLER_NOT_FOUND));

		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new NotFoundException(NOT_FOUND_ORDER));

		Long sellerIdInStore = order.getStore().getSeller().getId();

		//셀러 스토어가 같은 업장인지
		if (!sellerIdInStore.equals(seller.getId())) {
			throw new ForbiddenException(NOT_OWNER_OF_STORE);
		}

		order.accept(req.getReadyAt());

		return AcceptOrderRes.of(order);
	}
	//주문 거절

	@Transactional
	public RejectOrderRes rejectOrder(Long orderId, AuthUser authUser) {

		Seller seller = sellerRepository.findById(authUser.getId())
			.orElseThrow(() -> new NotFoundException(SELLER_NOT_FOUND));

		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new NotFoundException(NOT_FOUND_ORDER));

		Long sellerIdInStore = order.getStore().getSeller().getId();

		//셀러 스토어가 같은 업장인지
		if (!sellerIdInStore.equals(seller.getId())) {
			throw new ForbiddenException(NOT_OWNER_OF_STORE);
		}

		order.reject();

		return RejectOrderRes.of(order);
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteOrder(AuthUser authUser, Long orderId, RefundPaymentReq refundPaymentReq) throws IamportResponseException, IOException {
		User user = userRepository.findByEmail(authUser.getEmail())
			.orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

		Order order = orderRepository.findNotDeletedById(orderId)
			.orElseThrow(() -> new NotFoundException(NOT_FOUND_ORDER));

		if (!user.getId().equals(order.getUser().getId())) {
			throw new InvalidRequestException(NOT_OWNER_OF_ORDER);
		}

		//주문 취소 불가능 시간 --> Seller측에서 주문을 승인한 경우
		if (ACCEPTED.equals(order.getStatus())) {
			throw new InvalidRequestException(ALREADY_ACCEPTED);
		}

		Payment payment = paymentRepository.findNotDeletedByOrderId(order.getId())
				.orElseThrow(() -> new NotFoundException(NOT_FOUND_PAYMENT));

		order.delete();
		refundPayment(payment, refundPaymentReq.getImpUid());

		List<OrderHistory> orderHistories = orderHistoryRepository.findAllByOrderId(orderId);
		productService.rollBackStock(orderHistories);
	}

	@Transactional
	public void deleteOrderWithZeroPay(AuthUser authUser, Long orderId, ZPRefundPaymentReq zpRefundPaymentReq) {
		User user = userRepository.findByEmail(authUser.getEmail())
				.orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

		Order order = orderRepository.findNotDeletedById(orderId)
				.orElseThrow(() -> new NotFoundException(NOT_FOUND_ORDER));

		if (!user.getId().equals(order.getUser().getId())) {
			throw new InvalidRequestException(NOT_OWNER_OF_ORDER);
		}

		//주문 취소 불가능 시간 --> Seller측에서 주문을 승인한 경우
		if (OrderStatus.ACCEPTED.equals(order.getStatus())) {
			throw new InvalidRequestException(ALREADY_ACCEPTED);
		}

		Payment payment = paymentRepository.findNotDeletedByOrderId(order.getId())
				.orElseThrow(() -> new NotFoundException(NOT_FOUND_PAYMENT));

		if (PayStatus.CANCELED.equals(payment.getPayStatus())) {
			throw new InvalidRequestException(FAILED_CANCEL);
		}

		order.delete();
		refundPaymentWithZeroPay(payment, zpRefundPaymentReq.getMerchantUid());

		List<OrderHistory> orderHistories = orderHistoryRepository.findAllByOrderId(orderId);
		productService.rollBackStock(orderHistories);
	}

	private void refundPaymentWithZeroPay(Payment payment, String merchantUid) {
		if (!payment.getMerchantUid().equals(merchantUid)) {
			throw new InvalidRequestException(NOT_OWNER_OF_PAYMENT);
		}

		payment.cancel();
	}

	private void refundPayment(Payment payment,String impUid) throws IamportResponseException, IOException {
		payment.cancel();
		CancelData cancelData = new CancelData(impUid, true);
		IamportResponse<com.siot.IamportRestClient.response.Payment> cancelResponse = iamportClient.cancelPaymentByImpUid(cancelData);

		if(!"cancelled".equals(cancelResponse.getResponse().getStatus())){
			throw new InvalidRequestException(FAILED_CANCEL);
		}
	}

	private BigDecimal getTotalPrice(Map<Long, Product> productMap, List<CartItem> cartItemList) {
		BigDecimal totalPrice = BigDecimal.ZERO;

		for (CartItem item : cartItemList) {
			Product product = productMap.get(item.getProductId());
			if (product == null) {
				throw new NotFoundException(PRODUCT_NOT_FOUND);
			}
			BigDecimal itemTotal  = BigDecimal.valueOf(product.getPrice()).multiply(BigDecimal.valueOf(item.getQuantity()));
			totalPrice = totalPrice.add(itemTotal);
		}

		return totalPrice;
	}

	private String creatOrderName(Map<Long, Product> productMap) {
		int size = productMap.size();
		Product product = productMap.values().iterator().next();

		if (size == 1) {
			return String.format("%s", product.getName());
		}

		return String.format("%s 외 %d", product.getName(), size - 1);
	}
}
