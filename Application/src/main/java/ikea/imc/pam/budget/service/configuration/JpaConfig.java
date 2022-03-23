package ikea.imc.pam.budget.service.configuration;

import ikea.imc.pam.budget.service.repository.AuditorAwareImpl;
import ikea.imc.pam.budget.service.service.SecurityContextService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaConfig {

    private final SecurityContextService securityContextService;

    public JpaConfig(SecurityContextService securityContextService) {
        this.securityContextService = securityContextService;
    }

    @Bean
    public AuditorAware<String> auditorAware() {
        return new AuditorAwareImpl(securityContextService);
    }
}
