package api.buyhood.auth.service;

import api.buyhood.auth.dto.req.SignupUserReq;
import api.buyhood.auth.dto.res.SignupUserRes;
import api.buyhood.domain.user.entity.User;
import api.buyhood.domain.user.repository.UserRepository;
import api.exception.ConflictException;
import api.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static api.errorcode.AuthErrorCode.USER_EMAIL_DUPLICATED;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtProvider jwtProvider;

	@Transactional
	public SignupUserRes signUpUser(
		SignupUserReq signupUserReq
	) {
		if (userRepository.existsByEmail(signupUserReq.getEmail())) {
			throw new ConflictException(USER_EMAIL_DUPLICATED);
		}

		String encodedPassword = passwordEncoder.encode(signupUserReq.getPassword());

		User newUser = User.builder()
			.email(signupUserReq.getEmail())
			.password(encodedPassword)
			.username(signupUserReq.getUsername())
			.phoneNumber(signupUserReq.getPhoneNumber())
			.address(signupUserReq.getAddress())
			.build();

		User savedUser = userRepository.save(newUser);

		String accessToken = jwtProvider.createToken(
			savedUser.getId(),
			savedUser.getEmail(),
			savedUser.getRole());

		return new SignupUserRes(accessToken);
	}

}
