package api.buyhood.order.service;

import api.buyhood.cart.dto.response.CartRes;
import api.buyhood.cart.entity.Cart;
import api.buyhood.cart.entity.CartItem;
import api.buyhood.cart.repository.CartRepository;
import api.buyhood.domain.user.entity.User;
import api.buyhood.domain.user.repository.UserRepository;
import api.buyhood.errorcode.PaymentErrorCode;
import api.buyhood.exception.ForbiddenException;
import api.buyhood.exception.InvalidRequestException;
import api.buyhood.exception.NotFoundException;
import api.buyhood.order.dto.request.AcceptOrderReq;
import api.buyhood.order.dto.request.ApplyOrderReq;
import api.buyhood.order.dto.request.RefundPaymentReq;
import api.buyhood.order.dto.request.ZPRefundPaymentReq;
import api.buyhood.order.dto.response.AcceptOrderRes;
import api.buyhood.order.dto.response.ApplyOrderRes;
import api.buyhood.order.dto.response.RejectOrderRes;
import api.buyhood.order.entity.Order;
import api.buyhood.order.entity.OrderHistory;
import api.buyhood.order.enums.OrderStatus;
import api.buyhood.order.repository.OrderHistoryRepository;
import api.buyhood.order.repository.OrderRepository;
import api.buyhood.payment.enums.PayStatus;
import api.buyhood.payment.repository.PaymentRepository;
import api.buyhood.product.entity.Product;
import api.buyhood.product.repository.ProductRepository;
import api.buyhood.product.service.ProductService;
import api.buyhood.security.AuthUser;
import api.buyhood.store.entity.Store;
import api.buyhood.store.repository.StoreRepository;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import api.buyhood.payment.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static api.buyhood.enums.UserRole.SELLER;
import static api.buyhood.errorcode.CartErrorCode.NOT_FOUND_CART;
import static api.buyhood.errorcode.OrderErrorCode.*;
import static api.buyhood.errorcode.PaymentErrorCode.*;
import static api.buyhood.errorcode.ProductErrorCode.OUT_OF_STOCK;
import static api.buyhood.errorcode.ProductErrorCode.PRODUCT_NOT_FOUND;
import static api.buyhood.errorcode.StoreErrorCode.STORE_NOT_FOUND;
import static api.buyhood.errorcode.UserErrorCode.ROLE_MISMATCH;
import static api.buyhood.errorcode.UserErrorCode.USER_NOT_FOUND;
import static api.buyhood.order.enums.OrderStatus.ACCEPTED;

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
    private final PaymentRepository paymentRepository;
    private final IamportClient iamportClient;
    private final OrderHistoryRepository orderHistoryRepository;

    //주문 요청
    @Transactional
    public ApplyOrderRes applyOrder(ApplyOrderReq req, AuthUser authUser) {

        Store store = storeRepository.findActiveStoreById(req.getStoreId())
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

        List<Integer> quantityList = cart.getCart().stream()
                .map(CartItem::getQuantity)
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

        productService.decreaseStock(productIdList, quantityList,productMap);

        return ApplyOrderRes.of(order.getStore().getId(), CartRes.of(cart), order.getPaymentMethod(),
                order.getTotalPrice(), order.getStatus(), order.getCreatedAt(), order.getRequestMessage());
    }

    //주문 승인

    @Transactional
    public AcceptOrderRes acceptOrder(AcceptOrderReq req, Long orderId, AuthUser authUser) {

        User user = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

        if (!SELLER.equals(user.getRole())) {
            throw new ForbiddenException(ROLE_MISMATCH);
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_ORDER));

        Payment payment = paymentRepository.findNotDeletedByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_PAYMENT));

        if (!payment.isPaid()) {
            throw new InvalidRequestException(CANNOT_ACCEPT_ORDER);
        }

        Long sellerIdInStore = order.getStore().getSellerId();

        //셀러 스토어가 같은 업장인지
        if (!sellerIdInStore.equals(user.getId())) {
            throw new ForbiddenException(NOT_OWNER_OF_STORE);
        }

        order.accept(req.getReadyAt());

        return AcceptOrderRes.of(order);
    }
    //주문 거절

    @Transactional
    public RejectOrderRes rejectOrder(Long orderId, AuthUser authUser) {

        User user = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

        if (!SELLER.equals(user.getRole())) {
            throw new ForbiddenException(ROLE_MISMATCH);
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_ORDER));

        Long sellerIdInStore = order.getStore().getSellerId();

        //셀러 스토어가 같은 업장인지
        if (!sellerIdInStore.equals(user.getId())) {
            throw new ForbiddenException(NOT_OWNER_OF_STORE);
        }

        order.reject();

        return RejectOrderRes.of(order);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteOrder(AuthUser authUser, Long orderId, RefundPaymentReq refundPaymentReq)
            throws IamportResponseException, IOException {
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

        Map<Product, Integer> orderHistoryMap = new HashMap<>();
        List<OrderHistory> orderHistories = orderHistoryRepository.findAllByOrderId(orderId);
        for (OrderHistory orderHistory : orderHistories) {
            orderHistoryMap.put(orderHistory.getProduct(), orderHistory.getQuantity());
        }

        productService.rollBackStock(orderHistoryMap);
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
        if (ACCEPTED.equals(order.getStatus())) {
            throw new InvalidRequestException(ALREADY_ACCEPTED);
        }

        Payment payment = paymentRepository.findNotDeletedByOrderId(order.getId())
                .orElseThrow(() -> new NotFoundException(PaymentErrorCode.NOT_FOUND_PAYMENT));

        if (PayStatus.CANCELED.equals(payment.getPayStatus())) {
            throw new InvalidRequestException(PaymentErrorCode.FAILED_CANCEL);
        }

        order.delete();
        refundPaymentWithZeroPay(payment, zpRefundPaymentReq.getMerchantUid());

        Map<Product, Integer> orderHistoryMap = new HashMap<>();
        List<OrderHistory> orderHistories = orderHistoryRepository.findAllByOrderId(orderId);
        for (OrderHistory orderHistory : orderHistories) {
            orderHistoryMap.put(orderHistory.getProduct(), orderHistory.getQuantity());
        }
        productService.rollBackStock(orderHistoryMap);
    }

    private void refundPayment(Payment payment, String impUid) throws IamportResponseException, IOException {
        payment.cancel();
        CancelData cancelData = new CancelData(impUid, true);
        IamportResponse<com.siot.IamportRestClient.response.Payment> cancelResponse = iamportClient.cancelPaymentByImpUid(
                cancelData);

        if (!"cancelled".equals(cancelResponse.getResponse().getStatus())) {
            throw new InvalidRequestException(FAILED_CANCEL);
        }
    }

    private void refundPaymentWithZeroPay(Payment payment, String merchantUid) {
        if (!payment.getMerchantUid().equals(merchantUid)) {
            throw new InvalidRequestException(NOT_OWNER_OF_PAYMENT);
        }

        payment.cancel();
    }

    private BigDecimal getTotalPrice(Map<Long, Product> productMap, List<CartItem> cartItemList) {
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartItem item : cartItemList) {
            Product product = productMap.get(item.getProductId());
            if (product == null) {
                throw new NotFoundException(PRODUCT_NOT_FOUND);
            }
            BigDecimal itemTotal = BigDecimal.valueOf(product.getPrice())
                    .multiply(BigDecimal.valueOf(item.getQuantity()));
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
