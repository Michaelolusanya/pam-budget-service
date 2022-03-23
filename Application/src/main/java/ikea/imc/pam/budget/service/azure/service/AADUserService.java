package ikea.imc.pam.budget.service.azure.service;

import ikea.imc.pam.budget.service.azure.client.MicrosoftGraphClient;
import ikea.imc.pam.budget.service.azure.repository.model.AADUserInformation;
import ikea.imc.pam.budget.service.repository.model.UserInformation;
import ikea.imc.pam.budget.service.service.UserService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "ikea.imc.pam.oauth", name = "enabled", havingValue = "true")
public class AADUserService implements UserService {

    private final MicrosoftGraphClient client;

    public AADUserService(MicrosoftGraphClient microsoftGraphClient) {
        this.client = microsoftGraphClient;
    }

    @Cacheable("azure_user")
    @Override
    public UserInformation getUserInformation(String userId) {
        return new AADUserInformation(client.getUser(userId));
    }
}
