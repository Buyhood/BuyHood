package api.buyhood.domain.auth.controller;

import api.buyhood.domain.auth.dto.req.SignupUserReq;
import api.buyhood.domain.auth.dto.res.SignupUserRes;
import api.buyhood.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

	@PostMapping("/v1/auth/signup")
	public ResponseEntity<SignupUserRes> signup(
		@Valid @RequestBody SignupUserReq signupUserReq
	) {
		SignupUserRes signupUserRes = authService.signUpUser(signupUserReq);

		String accessToken = signupUserRes.getToken();

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + accessToken);

		return new ResponseEntity<>(signupUserRes, headers, HttpStatus.CREATED);
	}
}
