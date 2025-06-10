package api.buyhood.filter;

import api.buyhood.enums.UserRole;
import api.buyhood.exception.FilterAuthenticationException;
import api.buyhood.security.AuthUser;
import api.buyhood.security.JwtProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import static api.buyhood.errorcode.AuthErrorCode.INVALID_SIGNATURE_TOKEN;
import static api.buyhood.errorcode.AuthErrorCode.INVALID_TOKEN;
import static api.buyhood.errorcode.AuthErrorCode.UNSUPPORTED_TOKEN;
import static api.buyhood.errorcode.ServerErrorCode.INTERNAL_SERVER_ERROR;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtProvider jwtProvider;
	private static final AntPathMatcher antPathMatcher = new AntPathMatcher();

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		String contextPath = request.getContextPath(); // 컨텍스트 경로 확인
		String path = request.getServletPath();
		log.info("Context Path: {}, Servlet Path: {}", contextPath, path);
		return antPathMatcher.match("/internal/**", path);
	}

	@Override
	protected void doFilterInternal(
		@NonNull HttpServletRequest request,
		@NonNull HttpServletResponse response,
		@NonNull FilterChain filterChain
	) throws ServletException, IOException {

		String authHeader = request.getHeader("Authorization");

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			try {
				Claims claims = jwtProvider.extractClaims(token);
				if (SecurityContextHolder.getContext().getAuthentication() == null) {
					setAuthentication(claims);
				}
			} catch (ExpiredJwtException e) {
				log.error("Expired JWT token, 만료된 JWT 토큰입니다.", e);
				throw new FilterAuthenticationException(INVALID_TOKEN);
			} catch (SecurityException | MalformedJwtException e) {
				log.error("Invalid JWT signature, 유효하지 않은 JWT 서명입니다.", e);
				throw new FilterAuthenticationException(INVALID_SIGNATURE_TOKEN);
			} catch (UnsupportedJwtException e) {
				log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.", e);
				throw new FilterAuthenticationException(UNSUPPORTED_TOKEN);
			} catch (Exception e) {
				log.error("Internal server error", e);
				throw new FilterAuthenticationException(INTERNAL_SERVER_ERROR);
			}
		}
		filterChain.doFilter(request, response);
	}

	private void setAuthentication(Claims claims) {
		Long userId = Long.valueOf(claims.getSubject());
		String email = claims.get("email", String.class);
		UserRole userRole = UserRole.of(claims.get("role", String.class));

		AuthUser authUser = new AuthUser(userId, email, userRole);
		JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(authUser);

		SecurityContextHolder.getContext().setAuthentication(authenticationToken);

	}

}

