package api.buyhood.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"api.buyhood"})
public class BuyHoodOrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(BuyHoodOrderApplication.class, args);
	}

}
