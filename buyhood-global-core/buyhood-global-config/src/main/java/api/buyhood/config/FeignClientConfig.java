package api.buyhood.config;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignClientConfig {

	@Bean
	public RequestInterceptor requestInterceptor() {
		return requestTemplate -> {
			ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

			if (attrs != null) {
				HttpServletRequest request = attrs.getRequest();
				String accessToken = request.getHeader("Authorization");

				if (accessToken != null) {
					requestTemplate.header("Authorization", accessToken);
				}
			}
		};
	}
}
