package ikea.imc.pam.budget.service.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Validated
@ConfigurationProperties("ikea.imc.pam.network")
@ConstructorBinding
public class NetworkProperties {

    /** The domain the app is configured towards */
    private final String domain;

    /** The port the domain is configured towards */
    @Max(65535)
    @Min(0)
    private final int port;

    public NetworkProperties(String domain, int port) {
        this.domain = domain;
        this.port = port;
    }

    public String getDomain() {
        return domain;
    }

    public int getPort() {
        return port;
    }
}
