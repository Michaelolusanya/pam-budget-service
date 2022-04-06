package ikea.imc.pam.budget.service.repository;

import ikea.imc.pam.budget.service.service.SecurityContextService;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    private final SecurityContextService securityContextService;

    public AuditorAwareImpl(SecurityContextService securityContextService) {
        this.securityContextService = securityContextService;
    }

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(securityContextService.getUserId());
    }
}
