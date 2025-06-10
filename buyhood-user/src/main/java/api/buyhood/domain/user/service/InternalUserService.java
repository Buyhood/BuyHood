package api.buyhood.domain.user.service;

import api.buyhood.domain.user.entity.User;
import api.buyhood.domain.user.repository.InternalUserRepository;
import api.buyhood.dto.user.UserFeignDto;
import api.buyhood.enums.UserRole;
import api.buyhood.errorcode.UserErrorCode;
import api.buyhood.exception.InvalidRequestException;
import api.buyhood.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InternalUserService {

	private final InternalUserRepository internalUserRepository;

	@Transactional(readOnly = true)
	public UserFeignDto getUserInternal(Long userId) {
		User getUser = internalUserRepository.findActiveUserById(userId)
			.orElseThrow(() -> new NotFoundException(UserErrorCode.USER_NOT_FOUND));

		if (getUser.getRole() != UserRole.USER) {
			throw new InvalidRequestException(UserErrorCode.ROLE_MISMATCH);
		}

		return new UserFeignDto(
			getUser.getId(), getUser.getUsername(), getUser.getEmail(), getUser.getRole(), getUser.getAddress());
	}

	@Transactional(readOnly = true)
	public UserFeignDto getSellerInternal(Long userId) {
		User getUser = internalUserRepository.findActiveUserById(userId)
			.orElseThrow(() -> new NotFoundException(UserErrorCode.USER_NOT_FOUND));

		if (getUser.getRole() != UserRole.SELLER) {
			throw new InvalidRequestException(UserErrorCode.ROLE_MISMATCH);
		}

		return new UserFeignDto(
			getUser.getId(), getUser.getUsername(), getUser.getEmail(), getUser.getRole(), getUser.getAddress());
	}

	@Transactional(readOnly = true)
	public Boolean existsById(Long userId) {
		return internalUserRepository.existsById(userId);
	}

}
