package org.imc.pam.boilerplate.properties;

import javax.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("org.imc.pam.paths")
@ConstructorBinding
public class PathProperties {

    @NotEmpty private final String basePath;
    @NotEmpty private final String fileStoragePath;
    @NotEmpty private final String logStoragePath;
    @NotEmpty private final String smokeTestPath;

    public PathProperties(
            String basePath, String fileStoragePath, String logStoragePath, String smokeTestPath) {
        this.basePath = basePath;
        this.fileStoragePath = fileStoragePath;
        this.logStoragePath = logStoragePath;
        this.smokeTestPath = smokeTestPath;
    }

    public String getBasePath() {
        return basePath;
    }

    public String getFileStoragePath() {
        return fileStoragePath;
    }

    public String getLogStoragePath() {
        return logStoragePath;
    }

    public String getSmokeTestPath() {
        return smokeTestPath;
    }
}
