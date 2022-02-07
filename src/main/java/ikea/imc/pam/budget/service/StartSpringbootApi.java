package ikea.imc.pam.budget.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("ikea.imc.pam.budget.service.configuration.properties")
public class StartSpringbootApi {
    public static void main(String[] args) {
        SpringApplication springbootApi = new SpringApplication(StartSpringbootApi.class);
        springbootApi.run(args);
    }
}