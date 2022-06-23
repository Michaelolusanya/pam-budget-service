package com.ikea.imc.pam.budget.service.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

@Validated
@ConfigurationProperties("com.ikea.imc.pam.openapi")
@ConstructorBinding
public record OpenApiProperties(Documentation documentation) {
    
    public record Documentation(@NotEmpty String openApiDocs, @NotEmpty String openApiJsonDoc) {}
}
