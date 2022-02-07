package ikea.imc.pam.budget.service.configuration.properties;

import javax.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("ikea.imc.pam.paths")
@ConstructorBinding
public class PathProperties {

    @NotEmpty
    private final String basePath;

    @NotEmpty
    private final String logStoragePath;

    public PathProperties(
            String basePath, String logStoragePath) {
        this.basePath = basePath;
        this.logStoragePath = logStoragePath;
    }

    public String getBasePath() {
        return basePath;
    }

    public String getLogStoragePath() {
        return logStoragePath;
    }

}
