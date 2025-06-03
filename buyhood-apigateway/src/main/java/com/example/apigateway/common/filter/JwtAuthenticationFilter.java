package com.example.apigateway.common.filter;

import com.example.common.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

	private final JwtUtil jwtUtil;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		String authHeader =
			exchange.getRequest().getHeaders().getFirst("Authorization");

		//인증이 필요없는 경로는 패스
		String path = exchange.getRequest().getURI().getPath();
		if (path.startsWith("/api/v1/auth/")) {
			return chain.filter(exchange);
		}

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			log.error("JWT 토큰을 찾을 수 없습니다.");
			exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
			return exchange.getResponse().setComplete();
		}

		String token = authHeader.substring(7);
		try {
			Claims claims = jwtUtil.extractClaims(token);
			//필요하면 헤더에 사용자 정보 추가하기
			ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
				.header("USER-Id", claims.getSubject())
				.header("USER-Role", claims.get("role", String.class))
				.build();

			return chain.filter(exchange.mutate().request(mutatedRequest).build());
		} catch (ExpiredJwtException e) {
			log.error("만료된 JWT token 입니다.", e);
			exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
			return exchange.getResponse().setComplete();
		} catch (SecurityException | MalformedJwtException e) {
			log.error("Invalid JWT signature", e);
			exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
			return exchange.getResponse().setComplete();
		} catch (UnsupportedJwtException e) {
			log.error("Unsupported JWT token", e);
			exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
			return exchange.getResponse().setComplete();
		} catch (Exception e) {
			log.error("Internal server error", e);
			exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
			return exchange.getResponse().setComplete();
		}
	}

	@Override
	public int getOrder() {
		return -100;
	}
}

