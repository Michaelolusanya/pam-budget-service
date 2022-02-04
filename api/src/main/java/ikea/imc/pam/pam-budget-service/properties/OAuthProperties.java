package org.imc.pam.boilerplate.properties;

import javax.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

/**
 * For OAUTH-variables defined in spring, see the
 *
 * @see org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties OAUTH
 *     for Swagger
 * @see org.springdoc.core.SwaggerUiOAuthProperties
 */
@Validated
@ConfigurationProperties("org.imc.pam.oauth")
@ConstructorBinding
public class OAuthProperties {

    @NotEmpty private final String ownerMail;

    private final Documentation documentation;
    private final Microsoft microsoft;
    private final ClientScope clientScope;

    public OAuthProperties(
            String ownerMail,
            Documentation documentation,
            Microsoft microsoft,
            ClientScope clientScope) {
        this.documentation = documentation;
        this.ownerMail = ownerMail;
        this.microsoft = microsoft;
        this.clientScope = clientScope;
    }

    public String getOwnerMail() {
        return ownerMail;
    }

    public Documentation getDocumentation() {
        return documentation;
    }

    public Microsoft getMicrosoft() {
        return microsoft;
    }

    public ClientScope getClientScope() {
        return clientScope;
    }

    /* TODO: Discuss proper naming for these values, clientscope does not explain what they are */
    public static class ClientScope {
        private final String id;
        private final String readScopeDesc;
        private final String writeScopeDesc;

        public ClientScope(String id, String readScopeDesc, String writeScopeDesc) {
            this.id = id;
            this.readScopeDesc = readScopeDesc;
            this.writeScopeDesc = writeScopeDesc;
        }

        public String getId() {
            return id;
        }

        public String getReadScopeDesc() {
            return readScopeDesc;
        }

        public String getWriteScopeDesc() {
            return writeScopeDesc;
        }

        public String getReadScope() {
            return createScope(readScopeDesc);
        }

        public String getWriteScope() {
            return createScope(writeScopeDesc);
        }

        private String createScope(String desc) {
            return "api://" + id + "/" + desc;
        }
    }

    public static class Microsoft {
        @NotEmpty private final String tenantId;
        @NotEmpty private final String authorizationUrl;
        @NotEmpty private final String tokenUrl;

        public Microsoft(String tenantId, String authorizationUrl, String tokenUrl) {
            this.tenantId = tenantId;
            this.authorizationUrl = authorizationUrl;
            this.tokenUrl = tokenUrl;
        }

        public String getTenantId() {
            return tenantId;
        }

        public String getAuthorizationUrl() {
            return authorizationUrl;
        }

        public String getTokenUrl() {
            return tokenUrl;
        }
    }

    public static class Documentation {

        @NotEmpty private final String openApiDocs;
        @NotEmpty private final String openApiJsonDoc;

        public Documentation(String openApiDocs, String openApiJsonDoc) {
            this.openApiDocs = openApiDocs;
            this.openApiJsonDoc = openApiJsonDoc;
        }

        public String getOpenApiJsonDoc() {
            return openApiJsonDoc;
        }

        public String getOpenApiDocs() {
            return openApiDocs;
        }
    }
}
