package api.security;

import api.enums.UserRole;
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
public class JwtProvider {

	private static final long TOKEN_EXPIRATION_TIME = 60 * 1000L * 30 * 24; //30분 * 24

	@Value("${jwt.secret.key}")
	private String secretKey;
	private Key key;

	@PostConstruct
	public void init() {
		byte[] bytes = Base64.getDecoder().decode(secretKey);
		key = Keys.hmacShaKeyFor(bytes); // HS256 자동 인식
	}

	public String createToken(Long userId, String email, UserRole role) {
		Date now = new Date();

		return Jwts.builder()
			.subject(String.valueOf(userId))
			.claim("email", email)
			.claim("role", role.getRole())
			.expiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME))
			.issuedAt(now)
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
