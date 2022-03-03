package ikea.imc.pam.budget.service.service;

import com.azure.spring.aad.AADOAuth2AuthenticatedPrincipal;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "ikea.imc.pam.oauth", name = "enabled", havingValue = "true")
public class AADSecurityContextService implements SecurityContextService {

    private static final String ATTRIBUTE_USERNAME = "preferred_username";
    private static final String ATTRIBUTE_USER_ID = "oid";

    @Override
    public String getUserId() {
        return getPrincipal().getAttribute(ATTRIBUTE_USER_ID);
    }

    @Override
    public String getUserName() {
        return getPrincipal().getAttribute(ATTRIBUTE_USERNAME);
    }

    @Override
    public String getToken() {
        return getPrincipal().getTokenValue();
    }

    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private AADOAuth2AuthenticatedPrincipal getPrincipal() {
        Authentication authentication = getAuthentication();
        if (authentication.getPrincipal() instanceof AADOAuth2AuthenticatedPrincipal) {
            return (AADOAuth2AuthenticatedPrincipal) authentication.getPrincipal();
        }

        throw new IllegalStateException(
                "Security context doesn't hold Authentication principals of AADOAuth2AuthenticatedPrincipal");
    }
}
