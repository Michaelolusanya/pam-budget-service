package ikea.imc.pam.budget.service.api.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ApiConfiguration {

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .codecs(configure -> configure.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
                .build();
    }
}
