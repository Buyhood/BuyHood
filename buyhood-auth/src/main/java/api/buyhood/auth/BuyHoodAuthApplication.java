package api.buyhood.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
	"api.buyhood"
})
public class BuyHoodAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(BuyHoodAuthApplication.class, args);
	}

}
