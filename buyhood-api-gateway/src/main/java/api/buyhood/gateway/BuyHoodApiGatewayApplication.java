package api.buyhood.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@SpringBootApplication
@EnableDiscoveryClient
public class BuyHoodApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(BuyHoodApiGatewayApplication.class, args);
	}

}
