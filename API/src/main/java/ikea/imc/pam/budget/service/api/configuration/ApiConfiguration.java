package ikea.imc.pam.budget.service.api.configuration;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class ApiConfiguration {

    @Bean
    public WebClient webClient(
            @Value("${ikea.imc.pam.budget.service.url}") String budgetServiceBaseUrl,
            @Value("${ikea.imc.pam.budget.service.timeout:3000}") int budgetServiceTimeout) {
        HttpClient httpClient =
                HttpClient.create()
                        .responseTimeout(Duration.ofMillis(budgetServiceTimeout))
                        .baseUrl(budgetServiceBaseUrl);

        return WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).build();
    }
}
