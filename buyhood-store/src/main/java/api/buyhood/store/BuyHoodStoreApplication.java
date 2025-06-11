package api.buyhood.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = {"api.buyhood.store.client"})
@SpringBootApplication(scanBasePackages = {"api.buyhood"})
public class BuyHoodStoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(BuyHoodStoreApplication.class, args);
	}

}
