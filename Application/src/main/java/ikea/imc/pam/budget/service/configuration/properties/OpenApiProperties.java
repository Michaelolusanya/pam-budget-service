package ikea.imc.pam.budget.service.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

@Validated
@ConfigurationProperties("ikea.imc.pam.openapi")
@ConstructorBinding
public class OpenApiProperties {
    private final Documentation documentation;

    public OpenApiProperties(Documentation documentation) {
        this.documentation = documentation;
    }

    public Documentation getDocumentation() {
        return documentation;
    }

    public static class Documentation {

        @NotEmpty private final String openApiDocs;
        @NotEmpty private final String openApiJsonDoc;

        public Documentation(String openApiDocs, String openApiJsonDoc) {
            this.openApiDocs = openApiDocs;
            this.openApiJsonDoc = openApiJsonDoc;
        }

        public String getOpenApiJsonDoc() {
            return openApiJsonDoc;
        }

        public String getOpenApiDocs() {
            return openApiDocs;
        }
    }
}
