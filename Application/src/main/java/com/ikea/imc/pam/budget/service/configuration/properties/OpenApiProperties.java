package com.ikea.imc.pam.budget.service.configuration.properties;

import javax.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("com.ikea.imc.pam.openapi")
@ConstructorBinding
public record OpenApiProperties(Documentation documentation) {

    public record Documentation(@NotEmpty String openApiDocs, @NotEmpty String openApiJsonDoc) {}
}
