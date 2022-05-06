package com.ikea.imc.pam.budget.service.configuration.properties;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("ikea.imc.pam.network")
@ConstructorBinding
public record NetworkProperties(String domain, @Max(65535) @Min(0) int port) {}
