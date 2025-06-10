package api.buyhood.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
	"api.buyhood.auth",
	"api.buyhood.domain.user", // UserRepository가 있는 패키지
	"api.buyhood.config", // buyhood-global-config 모듈의 패키지
	"api.buyhood.security",
	"api.buyhood.filter"
})
public class BuyhoodDomainAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(BuyhoodDomainAuthApplication.class, args);
	}

}
