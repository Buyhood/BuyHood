package api.buyhood.cart.service;

import api.buyhood.cart.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InternalCartService {
    private final CartRepository cartRepository;
}
