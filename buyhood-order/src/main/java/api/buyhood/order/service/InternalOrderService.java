package api.buyhood.order.service;

import api.buyhood.dto.order.OrderFeignDto;
import api.buyhood.exception.NotFoundException;
import api.buyhood.order.entity.Order;
import api.buyhood.order.repository.InternalOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static api.buyhood.errorcode.OrderErrorCode.NOT_FOUND_ORDER;

@Service
@RequiredArgsConstructor
public class InternalOrderService {
    private final InternalOrderRepository internalOrderRepository;

    public OrderFeignDto findOrder(Long orderId) {
        Order order = internalOrderRepository.findNotDeletedById(orderId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_ORDER));

        return new OrderFeignDto(
                orderId,
                order.getUserId(),
                order.getStoreId(),
                order.getName(),
                String.valueOf(order.getPaymentMethod()),
                order.getTotalPrice(),
                String.valueOf(order.getStatus()),
                order.getRequestMessage(),
                order.getReadyAt()
        );
    }
}
