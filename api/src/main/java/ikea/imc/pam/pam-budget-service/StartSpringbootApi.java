package ikea.imc.pam.pam-budget-service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("org.imc.pam.boilerplate.properties")
public class StartSpringbootApi {
    public static void main(String[] args) {
        SpringApplication springbootApi = new SpringApplication(StartSpringbootApi.class);
        springbootApi.run(args);
    }
}
