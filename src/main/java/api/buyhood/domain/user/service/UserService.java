package api.buyhood.domain.user.service;

import api.buyhood.domain.auth.entity.AuthUser;
import api.buyhood.domain.user.dto.req.ChangePasswordReq;
import api.buyhood.domain.user.dto.req.DeleteUserReq;
import api.buyhood.domain.user.dto.req.PatchUserReq;
import api.buyhood.domain.user.dto.res.GetUserRes;
import api.buyhood.domain.user.dto.res.PatchUserRes;
import api.buyhood.domain.user.entity.User;
import api.buyhood.domain.user.repository.UserRepository;
import api.buyhood.global.common.exception.BadRequestException;
import api.buyhood.global.common.exception.NotFoundException;
import api.buyhood.global.common.exception.enums.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	//단건 조회
	@Transactional(readOnly = true)
	public GetUserRes getUser(Long id) {
		User user = userRepository.findById(id)
			.orElseThrow(() -> new NotFoundException(UserErrorCode.USER_NOT_FOUND));

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
	public void changePassword(AuthUser authUser, ChangePasswordReq changePasswordReq) {
		User user = userRepository.findByEmail(authUser.getEmail())
			.orElseThrow(() -> new NotFoundException(UserErrorCode.USER_NOT_FOUND));

		validateOldPassword(changePasswordReq.getOldPassword(), user.getPassword());
		validate(changePasswordReq.getNewPassword(), user.getPassword());
		user.changePassword(passwordEncoder.encode(changePasswordReq.getNewPassword()));
	}

	//회원 정보 변경
	@Transactional
	public PatchUserRes patchUser(AuthUser authUser, PatchUserReq patchUserReq) {
		User user = userRepository.findByEmail(authUser.getEmail())
			.orElseThrow(() -> new NotFoundException(UserErrorCode.USER_NOT_FOUND));

		user.patchUser(patchUserReq.getUsername(), patchUserReq.getAddress());

		return PatchUserRes.of(user);
	}

	//회원탈퇴
	@Transactional
	public void deleteUser(AuthUser authUser, DeleteUserReq deleteUserReq) {
		User findUser = userRepository.findByEmail(authUser.getEmail())
			.orElseThrow(() -> new NotFoundException(UserErrorCode.USER_NOT_FOUND));

		validateOldPassword(deleteUserReq.getPassword(), findUser.getPassword());
		findUser.deleteUser();
	}

	public void validateOldPassword(String rawPassword, String encodedPassword) {
		if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
			throw new BadRequestException(UserErrorCode.INVALID_PASSWORD);
		}
	}

	public void validate(String newPassword, String oldPassword) {
		if (newPassword.length() < 8
			|| !newPassword.matches(".*\\d.*")
			|| !newPassword.matches(".*[A-Z].*")) {
			throw new BadRequestException(UserErrorCode.INVALID_NEW_PASSWORD_FORMAT);
		}

		if (passwordEncoder.matches(newPassword, oldPassword)) {
			throw new BadRequestException(UserErrorCode.PASSWORD_SAME_AS_OLD);
		}
	}
}
