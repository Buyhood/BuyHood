package api.buyhood.payment;

import com.siot.IamportRestClient.IamportClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@EnableFeignClients(basePackages = "api.buyhood.payment.client")
@SpringBootApplication(scanBasePackages = {
        "api.buyhood.payment",
        "api.buyhood.config",
        "api.buyhood.security",
        "api.buyhood.filter"
})
public class BuyhoodPaymentApplication {

    public static void main(String[] args) {
        SpringApplication.run(BuyhoodPaymentApplication.class, args);
    }

}
