package api.buyhood.product.controller;

import api.buyhood.dto.product.request.GetProductReq;
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

}
