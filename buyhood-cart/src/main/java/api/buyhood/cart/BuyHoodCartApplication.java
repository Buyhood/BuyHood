package api.buyhood.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "api.buyhood.cart.client")
@SpringBootApplication(scanBasePackages = "api.buyhood")
public class BuyHoodCartApplication {

    public static void main(String[] args) {
        SpringApplication.run(BuyHoodCartApplication.class, args);
    }

}
