package com.ikea.imc.pam.budget.service.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Validated
@ConfigurationProperties("com.ikea.imc.pam.network")
@ConstructorBinding
public record NetworkProperties(String domain, @Max(65535) @Min(0) int port) {}
