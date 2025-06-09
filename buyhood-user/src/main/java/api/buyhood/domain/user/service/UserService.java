package api.buyhood.domain.user.service;

import api.buyhood.domain.user.dto.req.ChangeSellerRoleReq;
import api.buyhood.domain.user.dto.req.ChangeUserPasswordReq;
import api.buyhood.domain.user.dto.req.DeleteUserReq;
import api.buyhood.domain.user.dto.req.PatchUserReq;
import api.buyhood.domain.user.dto.res.GetUserRes;
import api.buyhood.domain.user.dto.res.PatchUserRes;
import api.buyhood.domain.user.entity.User;
import api.buyhood.domain.user.repository.UserRepository;
import api.buyhood.enums.UserRole;
import api.buyhood.exception.InvalidRequestException;
import api.buyhood.exception.NotFoundException;
import api.buyhood.security.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static api.buyhood.errorcode.UserErrorCode.ALREADY_SELLER;
import static api.buyhood.errorcode.UserErrorCode.BUSINESS_NUMBER_ALREADY_REGISTERED;
import static api.buyhood.errorcode.UserErrorCode.PASSWORD_SAME_AS_OLD;
import static api.buyhood.errorcode.UserErrorCode.USER_INVALID_PASSWORD;
import static api.buyhood.errorcode.UserErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	//단건 조회
	@Transactional(readOnly = true)
	public GetUserRes getUser(Long id) {
		User user = userRepository.findById(id)
			.orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

		return GetUserRes.of(user);
	}

	//다건 조회
	@Transactional(readOnly = true)
	public Page<GetUserRes> getAllUsers(int page, int size) {
		Page<User> users = userRepository.findAllActiveUsers(PageRequest.of(page, size));
		return users.map(GetUserRes::of);
	}

	//비밀번호 변경
	@Transactional
	public void changePassword(AuthUser authUser, ChangeUserPasswordReq changeUserPasswordReq) {
		User user = userRepository.findByEmail(authUser.getEmail())
			.orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

		validatePassword(changeUserPasswordReq.getOldPassword(), user.getPassword());
		if (passwordEncoder.matches(changeUserPasswordReq.getNewPassword(), user.getPassword())) {
			throw new InvalidRequestException(PASSWORD_SAME_AS_OLD);
		}
		user.changePassword(passwordEncoder.encode(changeUserPasswordReq.getNewPassword()));
	}

	//회원 정보 변경
	@Transactional
	public PatchUserRes patchUser(AuthUser authUser, PatchUserReq patchUserReq) {
		User user = userRepository.findByEmail(authUser.getEmail())
			.orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

		user.patchUser(patchUserReq.getUsername(), patchUserReq.getAddress());

		return PatchUserRes.of(user);
	}

	//회원탈퇴
	@Transactional
	public void deleteUser(AuthUser authUser, DeleteUserReq deleteUserReq) {
		User findUser = userRepository.findByEmail(authUser.getEmail())
			.orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

		validatePassword(deleteUserReq.getPassword(), findUser.getPassword());
		findUser.deleteUser();
	}

	private void validatePassword(String rawPassword, String encodedPassword) {
		if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
			throw new InvalidRequestException(USER_INVALID_PASSWORD);
		}
	}

	@Transactional
	public void grantSeller(Long userId, ChangeSellerRoleReq req) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

		if (user.getRole() == UserRole.SELLER) {
			throw new InvalidRequestException(ALREADY_SELLER);
		}
		// 다수의 사업자등록번호 입력방지
		if (user.getBusinessNumber() != null) {
			throw new InvalidRequestException(BUSINESS_NUMBER_ALREADY_REGISTERED);
		}

		user.registerBusinessNumber(req.getBusinessNumber());
		user.changeRole(UserRole.SELLER);
	}

	@Transactional
	public void grantAdmin(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
		user.changeRole(UserRole.ADMIN);
	}
}
