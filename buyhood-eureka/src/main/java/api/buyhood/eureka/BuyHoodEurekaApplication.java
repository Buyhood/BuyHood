package api.buyhood.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class BuyHoodEurekaApplication {

	public static void main(String[] args) {
		SpringApplication.run(BuyHoodEurekaApplication.class, args);
	}

}
