package api.buyhood.cart.controller;

import api.buyhood.cart.service.InternalCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal")
public class InternalCartController {
    private final InternalCartService internalCartService;
}
