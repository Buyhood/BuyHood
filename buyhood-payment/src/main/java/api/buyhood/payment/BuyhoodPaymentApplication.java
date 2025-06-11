package api.buyhood.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"api.buyhood.payment.client"})
public class BuyhoodPaymentApplication {

    public static void main(String[] args) {
        SpringApplication.run(BuyhoodPaymentApplication.class, args);
    }

}
