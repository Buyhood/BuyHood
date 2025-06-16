package api.buyhood.product.service;

import api.buyhood.dto.product.request.GetProductReq;
import api.buyhood.dto.product.response.ProductFeignDto;
import api.buyhood.exception.InvalidRequestException;
import api.buyhood.product.entity.Product;
import api.buyhood.product.repository.InternalProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static api.buyhood.errorcode.ProductErrorCode.PRODUCT_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class InternalProductService {
    private final InternalProductRepository internalProductRepository;

    @Transactional(readOnly = true)
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
}
