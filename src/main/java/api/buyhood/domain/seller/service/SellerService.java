package api.buyhood.domain.seller.service;

import api.buyhood.domain.seller.dto.res.GetSellerRes;
import api.buyhood.domain.seller.entity.Seller;
import api.buyhood.domain.seller.repository.SellerRepository;
import api.buyhood.global.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static api.buyhood.global.common.exception.enums.SellerErrorCode.SELLER_NOT_FOUND;

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
}
