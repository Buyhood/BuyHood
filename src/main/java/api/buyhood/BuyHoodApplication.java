package api.buyhood;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"api"})
public class BuyHoodApplication {

	public static void main(String[] args) {
		SpringApplication.run(BuyHoodApplication.class, args);
	}

}
