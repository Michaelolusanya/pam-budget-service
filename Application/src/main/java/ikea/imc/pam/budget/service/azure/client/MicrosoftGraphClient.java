package ikea.imc.pam.budget.service.azure.client;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.http.GraphServiceException;
import com.microsoft.graph.models.User;
import com.microsoft.graph.requests.GraphServiceClient;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MicrosoftGraphClient {

    private final TokenCredentialAuthProvider authenticationProvider;

    /**
     * Values are retrieved the same values used for authentication and to authenticate against another service Source:
     * com.azure.spring:azure-spring-boot-starter-active-directory
     *
     * @param clientId
     * @param clientSecret
     * @param tenantId
     */
    public MicrosoftGraphClient(
            @Value("${azure.activedirectory.client-id}") String clientId,
            @Value("${azure.activedirectory.client-secret}") String clientSecret,
            @Value("${azure.activedirectory.tenant-id}") String tenantId) {
        ClientSecretCredential clientSecretCredential =
                new ClientSecretCredentialBuilder()
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .tenantId(tenantId)
                        .build();

        this.authenticationProvider =
                new TokenCredentialAuthProvider(
                        List.of("https://graph.microsoft.com/.default"), clientSecretCredential);
    }

    public User getUser(String userId) {

        if (StringUtils.isBlank(userId)) {
            return generateAnonymousUser();
        }

        try {
            return getServiceClient().users(userId).buildRequest().get();
        } catch (GraphServiceException ex) {

            if (HttpStatus.NOT_FOUND.value() == ex.getResponseCode()) {
                log.info(ex.getMessage(), ex);
                return generateUnknownUser(userId);
            }

            log.error(ex.getMessage(), ex);
            return generateUnknownUser(userId);
        }
    }

    private User generateAnonymousUser() {
        User user = new User();
        user.id = "";
        user.givenName = "Anonymouse";
        user.surname = "User";
        return user;
    }

    private User generateUnknownUser(String userId) {
        User user = new User();
        user.id = userId;
        user.givenName = "Unknown";
        user.surname = "User";
        return user;
    }

    private GraphServiceClient<Request> getServiceClient() {
        return GraphServiceClient.builder().authenticationProvider(authenticationProvider).buildClient();
    }
}
