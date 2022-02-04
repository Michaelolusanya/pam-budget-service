package org.imc.pam.boilerplate.api.configuration;

import com.google.common.base.Joiner;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.*;
import io.swagger.v3.oas.models.servers.Server;
import org.imc.pam.boilerplate.properties.BoilerplateProperties;
import org.imc.pam.boilerplate.properties.NetworkProperties;
import org.imc.pam.boilerplate.properties.OAuthProperties;
import org.imc.pam.boilerplate.properties.OAuthProperties.ClientScope;
import org.imc.pam.boilerplate.tools.ApplicationContextUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class OpenapiConfiguration {

    String title = "boilerplate-java-springboot2-postgresql-api";

    private final NetworkProperties networkProperties;
    private final OAuthProperties oauthProperties;
    private final BoilerplateProperties boilerplateProperties;

    public OpenapiConfiguration(
            NetworkProperties networkProperties,
            OAuthProperties oauthProperties,
            BoilerplateProperties boilerplateProperties) {
        this.networkProperties = networkProperties;
        this.oauthProperties = oauthProperties;
        this.boilerplateProperties = boilerplateProperties;
    }

    @Bean
    public OpenAPI openAPI() {

        Components openapiComponents = getSecuritySchemesComponents();
        OpenAPI openapi = new OpenAPI();
        openapi.addServersItem(new Server().url(networkProperties.getDomain()));
        openapi.components(openapiComponents);
        openapi.setInfo(getApplicationInformation());
        return openapi;
    }

    private Info getApplicationInformation() {
        Contact contact = new Contact();
        contact.name("the application owner");
        contact.email(oauthProperties.getOwnerMail());

        Info info = new Info();
        info.title(title);
        info.description(getDescription());
        info.version(boilerplateProperties.getVersion());
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
                + "boilerplate-java-manager: "
                + boilerplateProperties.getJavaManagerURL()
                + "<br>";
    }

    /***************************************************************************************
     ******************************** Security Configuration ********************************
     ***************************************************************************************/

    private Components getSecuritySchemesComponents() {
        Parameter parameter = new Parameter();
        parameter.in("header");
        parameter.name("Version");
        parameter.schema(new StringSchema());
        parameter.required(false);

        Components components = new Components();
        components.addSecuritySchemes("Open Id Connect", getOpenIdConnectSecurityScheme());
        components.addSecuritySchemes("JWT-Token", getJwtTokenSecurityScheme());
        components.addParameters("Version", parameter);
        return components;
    }

    /***************************************************************************************
     *********************** Security Configuration: Open ID Connnect ***********************
     ***************************************************************************************/

    private SecurityScheme getOpenIdConnectSecurityScheme() {
        SecurityScheme secScheme = new SecurityScheme();
        secScheme.type(SecurityScheme.Type.OAUTH2);
        secScheme.description("Oauth2 flow");
        secScheme.flows(getAuthorizationCodeOAuthFlows());
        return secScheme;
    }

    private OAuthFlows getAuthorizationCodeOAuthFlows() {
        ClientScope clientScopeProperties = oauthProperties.getClientScope();
        Scopes scopes = new Scopes();
        scopes.addString(
                clientScopeProperties.getReadScope(), clientScopeProperties.getReadScopeDesc());
        scopes.addString(
                clientScopeProperties.getWriteScope(), clientScopeProperties.getWriteScopeDesc());

        OAuthFlow oAuthFlow = new OAuthFlow();
        oAuthFlow.setAuthorizationUrl(oauthProperties.getMicrosoft().getAuthorizationUrl());
        oAuthFlow.setTokenUrl(oauthProperties.getMicrosoft().getTokenUrl());
        oAuthFlow.scopes(scopes);

        OAuthFlows oauthFlows = new OAuthFlows();
        oauthFlows.authorizationCode(oAuthFlow);

        return oauthFlows;
    }

    /***************************************************************************************
     ************************** Security Configuration: JWT - Token *************************
     ***************************************************************************************/

    private SecurityScheme getJwtTokenSecurityScheme() {
        SecurityScheme secScheme = new SecurityScheme();
        secScheme.type(SecurityScheme.Type.HTTP);
        secScheme.description("jwt-bearer");
        secScheme.bearerFormat("JWT");
        secScheme.scheme("bearer");
        return secScheme;
    }
}
