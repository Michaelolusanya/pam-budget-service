package com.ikea.imc.pam.budget.service.configuration;

import com.google.common.base.Joiner;
import com.ikea.imc.pam.budget.service.configuration.properties.NetworkProperties;
import com.ikea.imc.pam.budget.service.configuration.properties.OAuthProperties;
import com.ikea.imc.pam.budget.service.util.ApplicationContextUtil;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class OpenapiConfiguration {

    String title = "pam-budget-service-api";

    private final NetworkProperties networkProperties;
    private final OAuthProperties oauthProperties;

    public OpenapiConfiguration(NetworkProperties networkProperties, OAuthProperties oauthProperties) {
        this.networkProperties = networkProperties;
        this.oauthProperties = oauthProperties;
    }

    @Bean
    public OpenAPI openAPI() {

        Components openapiComponents = getSecuritySchemesComponents();
        OpenAPI openapi = new OpenAPI();
        openapi.addServersItem(new Server().url(networkProperties.domain()));
        openapi.components(openapiComponents);
        openapi.setInfo(getApplicationInformation());
        openapi.addSecurityItem(new SecurityRequirement().addList("OAuth"));
        return openapi;
    }

    private Info getApplicationInformation() {
        Contact contact = new Contact();
        contact.name("the application owner");

        Info info = new Info();
        info.title(title);
        info.description(getDescription());
        info.version("");
        info.contact(contact);

        return info;
    }

    private String getDescription() {
        Environment environment = ApplicationContextUtil.getBean(Environment.class);
        String activeProfiles = Joiner.on(", ").join(environment.getActiveProfiles());
        return "<h3><strong>Environment:</strong><br>"
                + activeProfiles
                + "<br><br>"
                + "<strong>Description:</strong><br>"
                + "Budget Service API that is used by Hushålla BFF API "
                + "<br>";
    }

    /**
     * *************************************************************************************
     * ****************************** Security Configuration *******************************
     * *************************************************************************************
     */
    private Components getSecuritySchemesComponents() {
        Parameter parameter = new Parameter();
        parameter.in("header");
        parameter.name("Version");
        parameter.schema(new StringSchema());
        parameter.required(false);

        Components components = new Components();
        components.addSecuritySchemes("OAuth", getOpenIdConnectSecurityScheme());
        components.addSecuritySchemes("JWT-Token", getJwtTokenSecurityScheme());
        components.addParameters("Version", parameter);
        return components;
    }

    /**
     * ************************************************************************************* ********************
     * Security Configuration: Open ID Connnect ***********************
     * *************************************************************************************
     */
    private SecurityScheme getOpenIdConnectSecurityScheme() {
        SecurityScheme secScheme = new SecurityScheme();
        secScheme.type(SecurityScheme.Type.OAUTH2);
        secScheme.scheme("bearer");
        secScheme.bearerFormat("jwt");
        secScheme.in(SecurityScheme.In.HEADER);
        secScheme.name("Authorization");
        secScheme.description("Oauth2 flow");
        secScheme.flows(getAuthorizationCodeOAuthFlows());
        return secScheme;
    }

    private OAuthFlows getAuthorizationCodeOAuthFlows() {
        OAuthProperties.ClientScope clientScopeProperties = oauthProperties.getClientScope();
        Scopes scopes = new Scopes();
        scopes.addString(clientScopeProperties.getScope(), clientScopeProperties.scopeName());

        OAuthFlow oAuthFlow = new OAuthFlow();
        oAuthFlow.setAuthorizationUrl(oauthProperties.getMicrosoft().authorizationUrl());
        oAuthFlow.setTokenUrl(oauthProperties.getMicrosoft().tokenUrl());
        oAuthFlow.scopes(scopes);

        OAuthFlows oauthFlows = new OAuthFlows();
        oauthFlows.authorizationCode(oAuthFlow);

        return oauthFlows;
    }

    /**
     * *************************************************************************************
     * ************************ Security Configuration: JWT - Token ************************
     * *************************************************************************************
     */
    private SecurityScheme getJwtTokenSecurityScheme() {
        SecurityScheme secScheme = new SecurityScheme();
        secScheme.type(SecurityScheme.Type.HTTP);
        secScheme.description("jwt-bearer");
        secScheme.bearerFormat("JWT");
        secScheme.scheme("bearer");
        return secScheme;
    }
}
