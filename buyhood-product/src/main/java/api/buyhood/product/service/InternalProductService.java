package api.buyhood.product.service;

import api.buyhood.product.repository.InternalProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InternalProductService {
    private final InternalProductRepository internalProductRepository;
}
