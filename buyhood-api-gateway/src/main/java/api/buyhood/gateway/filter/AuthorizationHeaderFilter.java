package api.buyhood.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

	@Value("${jwt.secret.key}")
	private String jwtKey;

	public AuthorizationHeaderFilter() {
		super(Config.class);
	}

	public static class Config {

	}

	@Override
	public GatewayFilter apply(Config config) {
		return (exchange, chain) -> {
			ServerHttpRequest request = exchange.getRequest();

			if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
				return onError(exchange, "권한이 없습니다");
			}

			String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).getFirst();
			String jwt = authorizationHeader.replace("Bearer ", "");

			if (!isJwtValid(jwt)) {
				return onError(exchange, "토큰이 유효하지 않습니다.");
			}

			return chain.filter(exchange);
		};
	}

	private Mono<Void> onError(ServerWebExchange exchange, String errMessage) {
		ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(HttpStatus.UNAUTHORIZED);
		log.error(errMessage);

		return exchange.getResponse().setComplete();
	}

	private boolean isJwtValid(String jwt) {
		byte[] secretKeyBytes = Base64.getDecoder().decode(jwtKey);
		SecretKey signingKey = Keys.hmacShaKeyFor(secretKeyBytes);

		try {
			Jws<Claims> claims = Jwts.parser()
				.verifyWith(signingKey)
				.build()
				.parseSignedClaims(jwt);
		} catch (Exception e) {
			return false;
		}

		return true;
	}
}
