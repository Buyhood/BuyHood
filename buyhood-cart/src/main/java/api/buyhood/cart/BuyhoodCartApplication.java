package api.buyhood.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(
	scanBasePackages = {
		"api.buyhood.cart",
		"api.buyhood.config",
		"api.buyhood.filter",
		"api.buyhood.security"
	}
)
@EnableFeignClients(basePackages = {
    "api.buyhood.cart.client",  // Cart 모듈 내 Feign
    "api.buyhood.client"
})
public class BuyhoodCartApplication {

	public static void main(String[] args) {
		SpringApplication.run(BuyhoodCartApplication.class, args);
	}

}
