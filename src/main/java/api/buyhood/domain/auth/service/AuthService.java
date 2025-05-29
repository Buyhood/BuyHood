package api.buyhood.domain.auth.service;

import api.buyhood.domain.auth.dto.req.SignInUserReq;
import api.buyhood.domain.auth.dto.req.SignupUserReq;
import api.buyhood.domain.auth.dto.res.SignInUserRes;
import api.buyhood.domain.auth.dto.res.SignupUserRes;
import api.buyhood.domain.seller.repository.SellerRepository;
import api.buyhood.domain.user.entity.User;
import api.buyhood.domain.user.repository.UserRepository;
import api.buyhood.global.common.exception.ConflictException;
import api.buyhood.global.common.exception.NotFoundException;
import api.buyhood.global.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static api.buyhood.global.common.exception.enums.AuthErrorCode.EMAIL_DUPLICATED;
import static api.buyhood.global.common.exception.enums.AuthErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final SellerRepository sellerRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	@Transactional
	public SignupUserRes signUpUser(
		SignupUserReq signupUserReq
	) {
		if (userRepository.existsByEmail(signupUserReq.getEmail())) {
			throw new ConflictException(EMAIL_DUPLICATED);
		}

		String encodedPassword = passwordEncoder.encode(signupUserReq.getPassword());

		User newUser = User.builder()
			.email(signupUserReq.getEmail())
			.password(encodedPassword)
			.address(signupUserReq.getAddress())
			.username(signupUserReq.getUsername())
			.build();

		User savedUser = userRepository.save(newUser);

		String accessToken = jwtUtil.createToken(
			savedUser.getId(),
			savedUser.getEmail(),
			savedUser.getRole());

		return new SignupUserRes(accessToken);
	}

	@Transactional
	public SignInUserRes signinUser(
		SignInUserReq signInUserReq
	) {
		User user = userRepository.findByEmail(signInUserReq.getEmail())
			.orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

		String accessToken = jwtUtil.createToken(
			user.getId(),
			user.getEmail(),
			user.getRole());

		return new SignInUserRes(accessToken);
	}
}
