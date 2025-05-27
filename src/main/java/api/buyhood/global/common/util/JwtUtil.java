package api.buyhood.global.common.util;

import api.buyhood.domain.user.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

	private static final String BEARER_PREFIX = "Bearer ";
	private static final long TOKEN_EXPIRATION_TIME = 60 * 1000L * 30 * 24; //30분 * 24

	@Value("${jwt.secret.key}")
	private String secretKey;
	private Key key;

	//0.12버전부터 SignatureAlgorithm 따로 설정할필요없이 Key가 자동으로 인식함.

	@PostConstruct
	public void init() {
		byte[] bytes = Base64.getDecoder().decode(secretKey);
		key = Keys.hmacShaKeyFor(bytes); // HS256 자동 인식
	}

	public String createToken(Long userId, String username, String email, UserRole role) {
		return Jwts.builder()
			.subject(userId.toString())
			.claim("username", username)
			.claim("email", email)
			.claim("role", role.toString())
			.expiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME))
			.signWith(key)
			.compact();
	}

	public Claims extractClaims(String token) {
		return Jwts.parser()
			.verifyWith((SecretKey) key)
			.build()
			.parseSignedClaims(token)
			.getPayload();
	}
}
