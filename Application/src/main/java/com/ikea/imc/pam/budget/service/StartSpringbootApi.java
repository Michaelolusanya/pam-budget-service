package com.ikea.imc.pam.budget.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ConfigurationPropertiesScan("com.ikea.imc.pam.budget.service.configuration.properties")
@ComponentScan(basePackages = {"com.ikea.imc.pam.budget.service", "com.ikea.imc.pam.common"})
@EnableCaching
public class StartSpringbootApi {
    public static void main(String[] args) {
        SpringApplication springbootApi = new SpringApplication(StartSpringbootApi.class);
        springbootApi.run(args);
    }
}
