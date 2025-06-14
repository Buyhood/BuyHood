package api.buyhood.domain.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
	"api.buyhood"
})
public class BuyhoodDomainUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(BuyhoodDomainUserApplication.class, args);
	}

}
