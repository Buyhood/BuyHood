package api.buyhood.order.service;

import api.buyhood.cart.dto.response.CartRes;
import api.buyhood.cart.entity.Cart;
import api.buyhood.cart.entity.CartItem;
import api.buyhood.cart.repository.CartRepository;
import api.buyhood.dto.payment.PaymentFeignDto;
import api.buyhood.dto.store.StoreFeignDto;
import api.buyhood.dto.user.UserFeignDto;
import api.buyhood.errorcode.PaymentErrorCode;
import api.buyhood.exception.ForbiddenException;
import api.buyhood.exception.InvalidRequestException;
import api.buyhood.exception.NotFoundException;
import api.buyhood.order.client.PaymentFeignClient;
import api.buyhood.order.client.StoreFeignClient;
import api.buyhood.order.client.UserFeignClient;
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
import api.buyhood.product.entity.Product;
import api.buyhood.product.repository.ProductRepository;
import api.buyhood.product.service.ProductService;
import api.buyhood.security.AuthUser;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
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
import static api.buyhood.errorcode.PaymentErrorCode.FAILED_CANCEL;
import static api.buyhood.errorcode.PaymentErrorCode.NOT_OWNER_OF_PAYMENT;
import static api.buyhood.errorcode.ProductErrorCode.PRODUCT_NOT_FOUND;
import static api.buyhood.errorcode.UserErrorCode.ROLE_MISMATCH;
import static api.buyhood.order.enums.OrderStatus.ACCEPTED;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderHistoryService orderHistoryService;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final IamportClient iamportClient;
    private final OrderHistoryRepository orderHistoryRepository;
    private final StoreFeignClient storeFeignClient;
    private final UserFeignClient userFeignClient;
    private final PaymentFeignClient paymentFeignClient;

    //주문 요청
    @Transactional
    public ApplyOrderRes applyOrder(ApplyOrderReq req, AuthUser authUser) {

        StoreFeignDto store = storeFeignClient.getStoreOrElseThrow(req.getStoreId());
        UserFeignDto user = userFeignClient.getRoleUserOrElseThrow(authUser.getId());

        if (!cartRepository.existsCart(user.getUserId())) {
            throw new NotFoundException(NOT_FOUND_CART);
        }

        Cart cart = cartRepository.findCart(user.getUserId());

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
                .storeId(store.getStoreId())
                .userId(user.getUserId())
                .name(orderName)
                .paymentMethod(req.getPaymentMethod())
                .requestMessage(req.getRequestMessage())
                .status(OrderStatus.PENDING)
                .totalPrice(getTotalPrice(productMap, cart.getCart()))
                .build();
        orderRepository.save(order);
        orderHistoryService.saveOrderHistory(order, cart, productMap);
        cartRepository.clearCart(user.getUserId());

        productService.decreaseStock(productIdList, quantityList,productMap);

        return ApplyOrderRes.of(order.getStoreId(), CartRes.of(cart), order.getPaymentMethod(),
                order.getTotalPrice(), order.getStatus(), order.getCreatedAt(), order.getRequestMessage());
    }

    //주문 승인

    @Transactional
    public AcceptOrderRes acceptOrder(AcceptOrderReq req, Long orderId, AuthUser authUser) {

        UserFeignDto user = userFeignClient.getRoleUserOrElseThrow(authUser.getId());

        if (!SELLER.equals(user.getRole())) {
            throw new ForbiddenException(ROLE_MISMATCH);
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_ORDER));

        PaymentFeignDto payment = paymentFeignClient.findPayment(orderId);

        if (!"PAID".equals(payment.getPayStatus())) {
            throw new InvalidRequestException(CANNOT_ACCEPT_ORDER);
        }

        StoreFeignDto store = storeFeignClient.getStoreOrElseThrow(order.getStoreId());
        Long sellerIdInStore = store.getSellerId();

        //셀러 스토어가 같은 업장인지
        if (!sellerIdInStore.equals(user.getUserId())) {
            throw new ForbiddenException(NOT_OWNER_OF_STORE);
        }

        order.accept(req.getReadyAt());

        return AcceptOrderRes.of(order);
    }
    //주문 거절

    @Transactional
    public RejectOrderRes rejectOrder(Long orderId, AuthUser authUser) {

        UserFeignDto user = userFeignClient.getRoleUserOrElseThrow(authUser.getId());

        if (!SELLER.equals(user.getRole())) {
            throw new ForbiddenException(ROLE_MISMATCH);
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_ORDER));

        StoreFeignDto store = storeFeignClient.getStoreOrElseThrow(order.getStoreId());
        Long sellerIdInStore = store.getSellerId();

        //셀러 스토어가 같은 업장인지
        if (!sellerIdInStore.equals(user.getUserId())) {
            throw new ForbiddenException(NOT_OWNER_OF_STORE);
        }

        order.reject();

        return RejectOrderRes.of(order);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteOrder(AuthUser authUser, Long orderId, RefundPaymentReq refundPaymentReq)
            throws IamportResponseException, IOException {
        UserFeignDto user = userFeignClient.getRoleUserOrElseThrow(authUser.getId());

        Order order = orderRepository.findNotDeletedById(orderId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_ORDER));

        if (!user.getUserId().equals(order.getUserId())) {
            throw new InvalidRequestException(NOT_OWNER_OF_ORDER);
        }

        //주문 취소 불가능 시간 --> Seller측에서 주문을 승인한 경우
        if (ACCEPTED.equals(order.getStatus())) {
            throw new InvalidRequestException(ALREADY_ACCEPTED);
        }

        PaymentFeignDto payment = paymentFeignClient.findPayment(orderId);

        order.delete();
        paymentFeignClient.refundPayment(payment.getPaymentId());
        refundPayment(refundPaymentReq.getImpUid());

        Map<Long, Integer> orderHistoryMap = new HashMap<>();
        List<OrderHistory> orderHistories = orderHistoryRepository.findAllByOrderId(orderId);
        for (OrderHistory orderHistory : orderHistories) {
            orderHistoryMap.put(orderHistory.getProductId(), orderHistory.getQuantity());
        }

        productService.rollBackStock(orderHistoryMap);
    }

    @Transactional
    public void deleteOrderWithZeroPay(AuthUser authUser, Long orderId, ZPRefundPaymentReq zpRefundPaymentReq) {
        UserFeignDto user = userFeignClient.getRoleUserOrElseThrow(authUser.getId());

        Order order = orderRepository.findNotDeletedById(orderId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_ORDER));

        if (!user.getUserId().equals(order.getUserId())) {
            throw new InvalidRequestException(NOT_OWNER_OF_ORDER);
        }

        //주문 취소 불가능 시간 --> Seller측에서 주문을 승인한 경우
        if (ACCEPTED.equals(order.getStatus())) {
            throw new InvalidRequestException(ALREADY_ACCEPTED);
        }

        PaymentFeignDto payment = paymentFeignClient.findPayment(orderId);

        if ("CANCELED".equals(payment.getPayStatus())) {
            throw new InvalidRequestException(PaymentErrorCode.FAILED_CANCEL);
        }

        order.delete();
        paymentFeignClient.refundPayment(payment.getPaymentId());
        refundPaymentWithZeroPay(payment.getMerchantUid(), zpRefundPaymentReq.getMerchantUid());

        Map<Long, Integer> orderHistoryMap = new HashMap<>();
        List<OrderHistory> orderHistories = orderHistoryRepository.findAllByOrderId(orderId);
        for (OrderHistory orderHistory : orderHistories) {
            orderHistoryMap.put(orderHistory.getProductId(), orderHistory.getQuantity());
        }
        productService.rollBackStock(orderHistoryMap);
    }

    private void refundPayment(String impUid) throws IamportResponseException, IOException {
        CancelData cancelData = new CancelData(impUid, true);
        IamportResponse<com.siot.IamportRestClient.response.Payment> cancelResponse = iamportClient.cancelPaymentByImpUid(
                cancelData);

        if (!"cancelled".equals(cancelResponse.getResponse().getStatus())) {
            throw new InvalidRequestException(FAILED_CANCEL);
        }
    }

    private void refundPaymentWithZeroPay(String merchantUid, String validMerchantUid) {
        if (!merchantUid.equals(validMerchantUid)) {
            throw new InvalidRequestException(NOT_OWNER_OF_PAYMENT);
        }
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
