package api.buyhood.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "api.buyhood.payment.client")
@SpringBootApplication(scanBasePackages = {
        "api.buyhood.payment",
        "api.buyhood.config",
        "api.buyhood.security",
        "api.buyhood.filter"
})
public class BuyHoodPaymentApplication {

    public static void main(String[] args) {
        SpringApplication.run(BuyHoodPaymentApplication.class, args);
    }

}
