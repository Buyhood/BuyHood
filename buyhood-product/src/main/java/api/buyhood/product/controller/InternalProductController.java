package api.buyhood.product.controller;

import api.buyhood.dto.product.request.DecreaseStockProductReq;
import api.buyhood.dto.product.request.GetProductReq;
import api.buyhood.dto.product.request.RollbackStockProductReq;
import api.buyhood.dto.product.response.ProductFeignDto;
import api.buyhood.product.service.InternalProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal")
public class InternalProductController {

    private final InternalProductService internalProductService;

    @PostMapping("/v1/products")
    List<ProductFeignDto> getProductsOrElseThrow(@RequestBody GetProductReq getProductReq) {
        return internalProductService.getProducts(getProductReq);
    }

    @PostMapping("/v1/products/decrease")
    void decreaseStock(@RequestBody DecreaseStockProductReq decreaseStockProductReq){
        internalProductService.decreaseStock(decreaseStockProductReq);
    }

    @PostMapping("/v1/products/rollback")
    void rollbackStock(@RequestBody RollbackStockProductReq rollbackStockProductReq){
        internalProductService.rollbackStock(rollbackStockProductReq);
    }

}
