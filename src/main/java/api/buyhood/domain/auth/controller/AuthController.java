package api.buyhood.domain.auth.controller;

import api.buyhood.domain.auth.dto.req.SignInSellerReq;
import api.buyhood.domain.auth.dto.req.SignInUserReq;
import api.buyhood.domain.auth.dto.req.SignupSellerReq;
import api.buyhood.domain.auth.dto.req.SignupUserReq;
import api.buyhood.domain.auth.dto.res.SignInSellerRes;
import api.buyhood.domain.auth.dto.res.SignInUserRes;
import api.buyhood.domain.auth.dto.res.SignupSellerRes;
import api.buyhood.domain.auth.dto.res.SignupUserRes;
import api.buyhood.domain.auth.service.AuthService;
import api.buyhood.global.common.dto.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

	private final AuthService authService;

	//USER
	@PostMapping("/v1/auth/users/signup")
	public ResponseEntity<Response<SignupUserRes>> signup(
		@Valid @RequestBody SignupUserReq signupUserReq,
		HttpHeaders headers
	) {
		SignupUserRes signupUserRes = authService.signUpUser(signupUserReq);

		String accessToken = signupUserRes.getToken();

		headers.add("Authorization", "Bearer " + accessToken);

		return ResponseEntity.ok().headers(headers).body(Response.ok(signupUserRes));
	}

	//USER
	@PostMapping("/v1/auth/users/signin")
	public ResponseEntity<Response<SignInUserRes>> signIn(
		@Valid @RequestBody SignInUserReq signInUserReq,
		HttpHeaders headers
	) {
		SignInUserRes signInUserRes = authService.signinUser(signInUserReq);

		String accessToken = signInUserRes.getToken();

		headers.add("Authorization", "Bearer " + accessToken);

		return ResponseEntity.ok().headers(headers).body(Response.ok(signInUserRes));
	}

	//SELLER
	@PostMapping("/v1/auth/sellers/signup")
	public ResponseEntity<Response<SignupSellerRes>> signUpSeller(
		@Valid @RequestBody SignupSellerReq req,
		HttpHeaders headers
	) {
		SignupSellerRes res = authService.signUpSeller(req);

		String accessToken = res.getToken();

		headers.add("Authorization", "Bearer " + accessToken);

		return ResponseEntity.ok().headers(headers).body(Response.ok(res));
	}

	//SELLER
	@PostMapping("/v1/auth/sellers/signin")
	public ResponseEntity<Response<SignInSellerRes>> signIn(
		@Valid @RequestBody SignInSellerReq req,
		HttpHeaders headers
	) {
		SignInSellerRes res = authService.signinSeller(req);

		String accessToken = res.getToken();

		headers.add("Authorization", "Bearer " + accessToken);

		return ResponseEntity.ok().headers(headers).body(Response.ok(res));
	}
}
