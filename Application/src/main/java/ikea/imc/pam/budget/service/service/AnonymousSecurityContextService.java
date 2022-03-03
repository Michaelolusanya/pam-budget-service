package ikea.imc.pam.budget.service.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "ikea.imc.pam.oauth", name = "enabled", havingValue = "false")
public class AnonymousSecurityContextService implements SecurityContextService {
    @Override
    public String getUserId() {
        return "N/A";
    }

    @Override
    public String getUserName() {
        return "Anonymous";
    }

    @Override
    public String getToken() {
        return "";
    }
}
