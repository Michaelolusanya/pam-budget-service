package ikea.imc.pam.budget.service.service;

import ikea.imc.pam.budget.service.repository.model.SimpleUserInformation;
import ikea.imc.pam.budget.service.repository.model.UserInformation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/** This is a fallback implementation if authentication is disabled */
@Service
@ConditionalOnProperty(prefix = "ikea.imc.pam.oauth", name = "enabled", havingValue = "false")
public class SimpleUserService implements UserService {

    @Override
    public UserInformation getUserInformation(String userId) {
        return new SimpleUserInformation(userId);
    }
}
