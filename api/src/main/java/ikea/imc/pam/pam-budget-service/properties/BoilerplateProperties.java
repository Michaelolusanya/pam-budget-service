package org.imc.pam.boilerplate.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("org.imc.pam.boilerplate")
@ConstructorBinding
public class BoilerplateProperties {

    private final String javaManagerURL;
    private final String version;

    public BoilerplateProperties(String javaManagerURL, String version) {
        this.javaManagerURL = javaManagerURL;
        this.version = version;
    }

    public String getJavaManagerURL() {
        return javaManagerURL;
    }

    public String getVersion() {
        return version;
    }
}
