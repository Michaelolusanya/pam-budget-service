package com.ikea.imc.pam.budget.service.configuration;

import com.ikea.imc.pam.budget.service.repository.AuditorAwareImpl;
import com.ikea.imc.pam.common.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaConfig {
    
    private final UserService userService;
    
    public JpaConfig(UserService userService) {
        this.userService = userService;
    }
    
    @Bean
    public AuditorAware<String> auditorAware() {
        return new AuditorAwareImpl(userService);
    }
}
