package api.buyhood.cart.controller;

import api.buyhood.domain.cart.dto.request.CreateCartReq;
import api.buyhood.domain.cart.dto.response.CartRes;
import api.buyhood.domain.cart.service.CartService;
import api.buyhood.dto.Response;
import api.buyhood.security.AuthUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CartController {

	private final CartService cartService;

	/**
	 * 장바구니 담기 기능
	 */
	@PostMapping("/v1/carts")
	public Response<CartRes> addItemsToCart(
		@AuthenticationPrincipal AuthUser authUser,
		@Valid @RequestBody CreateCartReq createCartReq
	) {
		return Response.ok(cartService.addItemsToCart(authUser, createCartReq));
	}

	/**
	 * 장바구니 조회 기능
	 */
	@GetMapping("/v1/carts")
	public Response<CartRes> findCart(
		@AuthenticationPrincipal AuthUser authUser
	) {
		return Response.ok(cartService.findCart(authUser));
	}

	/**
	 * 장바구니 비우기
	 */
	@DeleteMapping("/v1/carts")
	public Response<String> clearCart(
		@AuthenticationPrincipal AuthUser authUser
	) {
		cartService.clearCart(authUser);
		return Response.ok("장바구니 비우기 성공");
	}
}
