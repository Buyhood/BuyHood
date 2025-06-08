package api.buyhood.domain.seller.service;

import api.buyhood.domain.seller.dto.req.ChangeSellerPasswordReq;
import api.buyhood.domain.seller.dto.req.DeleteSellerReq;
import api.buyhood.domain.seller.dto.res.GetSellerRes;
import api.buyhood.domain.seller.entity.Seller;
import api.buyhood.domain.seller.repository.SellerRepository;
import api.buyhood.exception.InvalidRequestException;
import api.buyhood.exception.NotFoundException;
import api.buyhood.security.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static api.buyhood.errorcode.SellerErrorCode.SELLER_NOT_FOUND;
import static api.buyhood.errorcode.UserErrorCode.PASSWORD_SAME_AS_OLD;
import static api.buyhood.errorcode.UserErrorCode.USER_INVALID_PASSWORD;
import static api.buyhood.errorcode.UserErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class SellerService {

	private final SellerRepository sellerRepository;
	private final PasswordEncoder passwordEncoder;

	//단건 조회
	@Transactional(readOnly = true)
	public GetSellerRes getUser(Long id) {
		Seller seller = sellerRepository.findById(id)
			.orElseThrow(() -> new NotFoundException(SELLER_NOT_FOUND));

		return GetSellerRes.of(seller);
	}

	//다건 조회
	@Transactional(readOnly = true)
	public Page<GetSellerRes> getAllSellers(int page, int size) {
		Page<Seller> sellers = sellerRepository.findAllActiveSellers(PageRequest.of(page, size));
		return sellers.map(GetSellerRes::of);
	}

	//회원 탈퇴
	@Transactional
	public void deleteSeller(AuthUser authUser, DeleteSellerReq req) {
		Seller findSeller = sellerRepository.findByEmail(authUser.getEmail())
			.orElseThrow(() -> new NotFoundException(SELLER_NOT_FOUND));

		validatePassword(req.getPassword(), findSeller.getPassword());
		findSeller.deleteSeller();

	}

	//비밀번호 변경
	@Transactional
	public void changePassword(AuthUser authUser, ChangeSellerPasswordReq req) {
		Seller seller = sellerRepository.findByEmail(authUser.getEmail())
			.orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

		validatePassword(req.getOldPassword(), seller.getPassword());
		if (passwordEncoder.matches(req.getNewPassword(), seller.getPassword())) {
			throw new InvalidRequestException(PASSWORD_SAME_AS_OLD);
		}
		seller.changePassword(passwordEncoder.encode(req.getNewPassword()));
	}

	private void validatePassword(String rawPassword, String encodedPassword) {
		if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
			throw new InvalidRequestException(USER_INVALID_PASSWORD);
		}
	}
}
