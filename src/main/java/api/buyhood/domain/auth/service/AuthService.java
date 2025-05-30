package api.buyhood.domain.auth.service;

import api.buyhood.domain.auth.dto.req.SignInSellerReq;
import api.buyhood.domain.auth.dto.req.SignInUserReq;
import api.buyhood.domain.auth.dto.req.SignupSellerReq;
import api.buyhood.domain.auth.dto.req.SignupUserReq;
import api.buyhood.domain.auth.dto.res.SignInSellerRes;
import api.buyhood.domain.auth.dto.res.SignInUserRes;
import api.buyhood.domain.auth.dto.res.SignupSellerRes;
import api.buyhood.domain.auth.dto.res.SignupUserRes;
import api.buyhood.domain.seller.entity.Seller;
import api.buyhood.domain.seller.repository.SellerRepository;
import api.buyhood.domain.user.entity.User;
import api.buyhood.domain.user.repository.UserRepository;
import api.buyhood.global.common.exception.ConflictException;
import api.buyhood.global.common.exception.InvalidRequestException;
import api.buyhood.global.common.exception.NotFoundException;
import api.buyhood.global.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static api.buyhood.global.common.exception.enums.AuthErrorCode.SELLER_EMAIL_DUPLICATED;
import static api.buyhood.global.common.exception.enums.AuthErrorCode.USER_EMAIL_DUPLICATED;
import static api.buyhood.global.common.exception.enums.SellerErrorCode.SELLER_INVALID_PASSWORD;
import static api.buyhood.global.common.exception.enums.SellerErrorCode.SELLER_NOT_FOUND;
import static api.buyhood.global.common.exception.enums.UserErrorCode.USER_INVALID_PASSWORD;
import static api.buyhood.global.common.exception.enums.UserErrorCode.USER_NOT_FOUND;

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
			throw new ConflictException(USER_EMAIL_DUPLICATED);
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
		SignInUserReq req
	) {
		User user = userRepository.findByEmail(req.getEmail())
			.orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

		if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
			throw new InvalidRequestException(USER_INVALID_PASSWORD);
		}

		String accessToken = jwtUtil.createToken(
			user.getId(),
			user.getEmail(),
			user.getRole());

		return new SignInUserRes(accessToken);
	}

	@Transactional
	public SignupSellerRes signUpSeller(
		SignupSellerReq signupSellerReq
	) {
		if (sellerRepository.existsByEmail(signupSellerReq.getEmail())) {
			throw new ConflictException(SELLER_EMAIL_DUPLICATED);
		}

		String encodedPassword = passwordEncoder.encode(signupSellerReq.getPassword());

		Seller newSeller = Seller.builder()
			.email(signupSellerReq.getEmail())
			.password(encodedPassword)
			.businessName(signupSellerReq.getBusinessName())
			.businessNumber(String.valueOf(signupSellerReq.getBusinessNumber()))
			.businessAddress(signupSellerReq.getBusinessAddress())
			.username(signupSellerReq.getUsername())
			.build();

		Seller savedSeller = sellerRepository.save(newSeller);

		String accessToken = jwtUtil.createToken(
			savedSeller.getId(),
			savedSeller.getEmail(),
			savedSeller.getRole());

		return new SignupSellerRes(accessToken);
	}

	@Transactional
	public SignInSellerRes signinSeller(
		SignInSellerReq req
	) {
		Seller seller = sellerRepository.findByEmail(req.getEmail())
			.orElseThrow(() -> new NotFoundException(SELLER_NOT_FOUND));

		if (!passwordEncoder.matches(req.getPassword(), seller.getPassword())) {
			throw new InvalidRequestException(SELLER_INVALID_PASSWORD);
		}

		String accessToken = jwtUtil.createToken(
			seller.getId(),
			seller.getEmail(),
			seller.getRole());

		return new SignInSellerRes(accessToken);
	}
}
