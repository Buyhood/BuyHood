package api.buyhood.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "api.buyhood.cart.client")
public class BuyhoodCartApplication {

    public static void main(String[] args) {
        SpringApplication.run(BuyhoodCartApplication.class, args);
    }

}
