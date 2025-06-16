package api.buyhood.product.service;

import api.buyhood.dto.product.request.DecreaseStockProductReq;
import api.buyhood.dto.product.request.GetProductReq;
import api.buyhood.dto.product.request.RollbackStockProductReq;
import api.buyhood.dto.product.response.ProductFeignDto;
import api.buyhood.errorcode.ProductErrorCode;
import api.buyhood.exception.InvalidRequestException;
import api.buyhood.exception.NotFoundException;
import api.buyhood.product.entity.Product;
import api.buyhood.product.repository.InternalProductRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static api.buyhood.errorcode.ProductErrorCode.OUT_OF_STOCK;
import static api.buyhood.errorcode.ProductErrorCode.PRODUCT_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class InternalProductService {
    private final InternalProductRepository internalProductRepository;

    public List<ProductFeignDto> getProducts(GetProductReq getProductReq) {
        List<Product> products = internalProductRepository.findAllById(getProductReq.getProductIdList());

        if (products.size() != getProductReq.getProductIdList().size()) {
            throw new InvalidRequestException(PRODUCT_NOT_FOUND);
        }

        return products.stream()
                .map(product -> new ProductFeignDto(
                        product.getId(),
                        product.getName(),
                        product.getPrice(),
                        product.getDescription(),
                        product.getStock(),
                        product.getStoreId())
                )
                .toList();
    }

    @Retryable(
            retryFor = {
                    OptimisticLockException.class,
                    ObjectOptimisticLockingFailureException.class
            },
            backoff = @Backoff(delay = 100)
    )
    @Transactional
    public void decreaseStock(DecreaseStockProductReq decreaseStockProductReq) {

        List<Long> productIdList = decreaseStockProductReq.getProductIdList();
        List<Integer> quantityList = decreaseStockProductReq.getQuantityList();
        Map<Long, ProductFeignDto> productMap = decreaseStockProductReq.getProductMap();


        for (int i = 0; i < productIdList.size(); i++) {
            Long productId = productIdList.get(i);
            Integer quantity = quantityList.get(i);

            ProductFeignDto productFeignDto = productMap.get(productId);
            Product product = internalProductRepository.findById(productFeignDto.getProductId())
                    .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));

            if (product.getStock() < quantity) {
                throw new InvalidRequestException(OUT_OF_STOCK);
            }

            product.decreaseStock(quantity);
        }
    }

    @Retryable(
            retryFor = {
                    OptimisticLockException.class,
                    ObjectOptimisticLockingFailureException.class
            },
            backoff = @Backoff(delay = 100)
    )
    @Transactional
    public void rollbackStock(RollbackStockProductReq rollbackStockProductReq) {

        Map<Long, Integer> orderHistories = rollbackStockProductReq.getOrderHistoryMap();

        for (Map.Entry<Long, Integer> entry : orderHistories.entrySet()) {
            Long productId = entry.getKey();
            Integer quantity = entry.getValue();

            Product product = internalProductRepository.findById(productId)
                    .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));

            product.rollBackStock(quantity);
        }
    }

    /* 주문 요청 후 재고 감소에 대한 recover */
    @Recover
    public void recover(OptimisticLockException e, List<Long> productIdList, List<Integer> quantityList,
                        Map<Long, Product> productMap) {
        throw new InvalidRequestException(ProductErrorCode.STOCK_UPDATE_CONFLICT);
    }

    @Recover
    public void recover(ObjectOptimisticLockingFailureException e, List<Long> productIdList, List<Integer> quantityList,
                        Map<Long, Product> productMap) {
        throw new InvalidRequestException(ProductErrorCode.STOCK_UPDATE_CONFLICT);
    }

    /* 주문 취소 후 재고 롤백에 대한 recover*/
    @Recover
    public void recover(OptimisticLockException e, Map<Long, Integer> orderHistories) {
        throw new InvalidRequestException(ProductErrorCode.STOCK_UPDATE_CONFLICT);
    }

    @Recover
    public void recover(ObjectOptimisticLockingFailureException e, Map<Long, Integer> orderHistories) {
        throw new InvalidRequestException(ProductErrorCode.STOCK_UPDATE_CONFLICT);
    }
}
