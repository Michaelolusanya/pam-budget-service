package com.ikea.imc.pam.budget.service.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

/**
 * For OAUTH-variables defined in spring, see the
 *
 * @see org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties OAUTH for Swagger
 * @see org.springdoc.core.SwaggerUiOAuthProperties
 */
@Validated
@ConfigurationProperties("com.ikea.imc.pam.oauth")
@ConstructorBinding
public class OAuthProperties {
    
    private final Microsoft microsoft;
    private final ClientScope clientScope;
    private final boolean enabled;
    
    public OAuthProperties(Microsoft microsoft, ClientScope clientScope, Boolean enabled) {
        
        this.microsoft = microsoft;
        this.clientScope = clientScope;
        this.enabled = enabled != null ? enabled : true;
    }
    
    public Microsoft getMicrosoft() {
        return microsoft;
    }
    
    public ClientScope getClientScope() {
        return clientScope;
    }
    
    public boolean getEnabled() {
        return enabled;
    }
    
    /*
     * TODO: Discuss proper naming for these values, clientscope does not explain
     * what they are
     */
    public record ClientScope(String id, String scopeName) {
        
        public String getScope() {
            return "api://" + id + "/" + scopeName;
        }
    }
    
    public record Microsoft(String authorizationUrl, String tokenUrl) {}
}
