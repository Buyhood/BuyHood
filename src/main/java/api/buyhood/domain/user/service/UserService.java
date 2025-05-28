package api.buyhood.domain.user.service;

import api.buyhood.domain.user.dto.res.GetUserRes;
import api.buyhood.domain.user.entity.User;
import api.buyhood.domain.user.repository.UserRepository;
import api.buyhood.global.common.exception.NotFoundException;
import api.buyhood.global.common.exception.enums.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

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
}
