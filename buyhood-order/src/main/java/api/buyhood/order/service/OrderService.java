package api.buyhood.order.service;

import api.buyhood.dto.cart.CartDto;
import api.buyhood.dto.cart.CartItemDto;
import api.buyhood.dto.cart.ExistsCartDto;
import api.buyhood.dto.payment.PaymentFeignDto;
import api.buyhood.dto.product.request.DecreaseStockProductReq;
import api.buyhood.dto.product.request.GetProductReq;
import api.buyhood.dto.product.request.RollbackStockProductReq;
import api.buyhood.dto.product.response.ProductFeignDto;
import api.buyhood.dto.store.StoreFeignDto;
import api.buyhood.dto.user.UserFeignDto;
import api.buyhood.errorcode.PaymentErrorCode;
import api.buyhood.exception.ForbiddenException;
import api.buyhood.exception.InvalidRequestException;
import api.buyhood.exception.NotFoundException;
import api.buyhood.order.client.*;
import api.buyhood.order.dto.request.*;
import api.buyhood.order.dto.response.AcceptOrderRes;
import api.buyhood.order.dto.response.ApplyOrderRes;
import api.buyhood.order.dto.response.RejectOrderRes;
import api.buyhood.order.entity.Order;
import api.buyhood.order.entity.OrderHistory;
import api.buyhood.order.enums.OrderStatus;
import api.buyhood.order.repository.OrderHistoryRepository;
import api.buyhood.order.repository.OrderRepository;
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
    private final IamportClient iamportClient;
    private final OrderHistoryRepository orderHistoryRepository;
    private final StoreFeignClient storeFeignClient;
    private final UserFeignClient userFeignClient;
    private final PaymentFeignClient paymentFeignClient;
    private final ProductFeignClient productFeignClient;
    private final CartFeignClient cartFeignClient;

    //주문 요청
    @Transactional
    public ApplyOrderRes applyOrder(ApplyOrderReq req, AuthUser authUser) {

        StoreFeignDto store = storeFeignClient.getStoreOrElseThrow(req.getStoreId());
        UserFeignDto user = userFeignClient.getRoleUserOrElseThrow(authUser.getId());
        ExistsCartDto existsCartDto = cartFeignClient.existsCart(user.getId());

        if (!existsCartDto.isExists()) {
            throw new NotFoundException(NOT_FOUND_CART);
        }

        CartDto cart = cartFeignClient.findCart(user.getId());

        List<Long> productIdList = cart.getCartList().stream()
                .map(CartItemDto::getProductId)
                .toList();

        List<Integer> quantityList = cart.getCartList().stream()
                .map(CartItemDto::getQuantity)
                .toList();

        Map<Long, ProductFeignDto> productMap = productFeignClient.getProductsOrElseThrow(new GetProductReq(productIdList)).stream()
                .collect(Collectors.toMap(ProductFeignDto::getProductId, productFeignDto -> productFeignDto));

        String orderName = creatOrderName(productMap);

        Order order = Order.builder()
                .storeId(store.getStoreId())
                .userId(user.getId())
                .name(orderName)
                .paymentMethod(req.getPaymentMethod())
                .requestMessage(req.getRequestMessage())
                .status(OrderStatus.PENDING)
                .totalPrice(getTotalPrice(productMap, cart.getCartList()))
                .build();
        orderRepository.save(order);
        orderHistoryService.saveOrderHistory(order, cart, productMap);
        cartFeignClient.clearCart(user.getId());

        productFeignClient.decreaseStock(new DecreaseStockProductReq(productIdList, quantityList, productMap));

        return ApplyOrderRes.of(order.getStoreId(), cart, order.getPaymentMethod(),
                order.getTotalPrice(), order.getStatus(), order.getCreatedAt(), order.getRequestMessage());
    }

    //주문 승인

    @Transactional
    public AcceptOrderRes acceptOrder(AcceptOrderReq req, Long orderId, AuthUser authUser) {

        UserFeignDto user = userFeignClient.getRoleSellerOrElseThrow(authUser.getId());

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_ORDER));

        PaymentFeignDto payment = paymentFeignClient.findPayment(orderId);

        if (!"PAID".equals(payment.getPayStatus())) {
            throw new InvalidRequestException(CANNOT_ACCEPT_ORDER);
        }

        StoreFeignDto store = storeFeignClient.getStoreOrElseThrow(order.getStoreId());
        Long sellerIdInStore = store.getSellerId();

        //셀러 스토어가 같은 업장인지
        if (!sellerIdInStore.equals(user.getId())) {
            throw new ForbiddenException(NOT_OWNER_OF_STORE);
        }

        order.accept(req.getReadyAt());

        return AcceptOrderRes.of(order);
    }
    //주문 거절

    @Transactional
    public RejectOrderRes rejectOrder(Long orderId, AuthUser authUser, RejectOrderReq rejectOrderReq) {

        UserFeignDto user = userFeignClient.getRoleSellerOrElseThrow(authUser.getId());

        if (!SELLER.equals(user.getRole())) {
            throw new ForbiddenException(ROLE_MISMATCH);
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_ORDER));

        PaymentFeignDto payment = paymentFeignClient.findPayment(orderId);

        StoreFeignDto store = storeFeignClient.getStoreOrElseThrow(order.getStoreId());
        Long sellerIdInStore = store.getSellerId();

        //셀러 스토어가 같은 업장인지
        if (!sellerIdInStore.equals(user.getId())) {
            throw new ForbiddenException(NOT_OWNER_OF_STORE);
        }

        order.reject();
        rollbackProductStock(orderId, user);
        paymentFeignClient.refundPayment(payment.getPaymentId());

        return RejectOrderRes.of(order, rejectOrderReq.getMessage());
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteOrder(AuthUser authUser, Long orderId, RefundPaymentReq refundPaymentReq)
            throws IamportResponseException, IOException {
        UserFeignDto user = userFeignClient.getRoleUserOrElseThrow(authUser.getId());

        Order order = orderRepository.findNotDeletedById(orderId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_ORDER));

        if (!user.getId().equals(order.getUserId())) {
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

        rollbackProductStock(orderId, user);
    }

    @Transactional
    public void deleteOrderWithZeroPay(AuthUser authUser, Long orderId, ZPRefundPaymentReq zpRefundPaymentReq) {
        UserFeignDto user = userFeignClient.getRoleUserOrElseThrow(authUser.getId());

        Order order = orderRepository.findNotDeletedById(orderId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_ORDER));

        if (!user.getId().equals(order.getUserId())) {
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

        rollbackProductStock(orderId, user);
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

    private BigDecimal getTotalPrice(Map<Long, ProductFeignDto> productMap, List<CartItemDto> cartItemList) {
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartItemDto item : cartItemList) {
            ProductFeignDto product = productMap.get(item.getProductId());
            if (product == null) {
                throw new NotFoundException(PRODUCT_NOT_FOUND);
            }
            BigDecimal itemTotal = BigDecimal.valueOf(product.getPrice())
                    .multiply(BigDecimal.valueOf(item.getQuantity()));
            totalPrice = totalPrice.add(itemTotal);
        }

        return totalPrice;
    }

    private String creatOrderName(Map<Long, ProductFeignDto> productMap) {
        int size = productMap.size();
        ProductFeignDto product = productMap.values().iterator().next();

        if (size == 1) {
            return String.format("%s", product.getProductName());
        }

        return String.format("%s 외 %d", product.getProductName(), size - 1);
    }

    private void rollbackProductStock(Long orderId, UserFeignDto user) {
        Map<Long, Integer> orderHistoryMap = new HashMap<>();
        List<OrderHistory> orderHistories = orderHistoryRepository.findAllByOrderIdAndUserId(orderId, user.getId());
        for (OrderHistory orderHistory : orderHistories) {
            orderHistoryMap.put(orderHistory.getProductId(), orderHistory.getQuantity());
        }

        productFeignClient.rollbackStock(new RollbackStockProductReq(orderHistoryMap));
    }
}
