package api.buyhood.domain.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
	"api.buyhood"
})
public class BuyHoodUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(BuyHoodUserApplication.class, args);
	}

}
