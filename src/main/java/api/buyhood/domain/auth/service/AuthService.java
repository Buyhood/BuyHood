package api.buyhood.domain.auth.service;

import api.buyhood.domain.auth.dto.req.SignupUserReq;
import api.buyhood.domain.auth.dto.res.SignupUserRes;
import api.buyhood.domain.seller.repository.SellerRepository;
import api.buyhood.domain.user.entity.User;
import api.buyhood.domain.user.enums.UserRole;
import api.buyhood.domain.user.repository.UserRepository;
import api.buyhood.global.common.exception.ConflictException;
import api.buyhood.global.common.exception.enums.AuthErrorCode;
import api.buyhood.global.common.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final SellerRepository sellerRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	@Transactional
	public SignupUserRes signUpUser(
		@Valid SignupUserReq signupUserReq
	) {
		if (userRepository.existsByEmail(signupUserReq.getEmail())) {
			throw new ConflictException(AuthErrorCode.EMAIL_DUPLICATED);
		}

		String password = passwordEncoder.encode(signupUserReq.getPassword());
		UserRole userRole = UserRole.USER;

		User newUser = User.builder()
			.email(signupUserReq.getEmail())
			.password(password)
			.role(userRole)
			.address(signupUserReq.getAddress())
			.username(signupUserReq.getUsername())
			.build();

		User savedUser = userRepository.save(newUser);

		String accessToken = jwtUtil.createToken(
			savedUser.getId(),
			savedUser.getUsername(),
			savedUser.getEmail(),
			savedUser.getRole());

		return new SignupUserRes(accessToken);
	}
}
