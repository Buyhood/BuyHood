package api.buyhood.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = {"api.buyhood.order.client"})
@SpringBootApplication(scanBasePackages = "api.buyhood.cart")
public class BuyHoodOrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(BuyHoodOrderApplication.class, args);
	}

}
