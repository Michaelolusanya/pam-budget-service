package ikea.imc.pam.budget.service.component.test;

import com.github.tomakehurst.wiremock.client.WireMock;
import java.io.File;
import javax.annotation.PostConstruct;

import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ContextConfiguration(classes = AbstractBaseTest.TestConfig.class)
public abstract class AbstractBaseTest {
    private static final Logger log = LoggerFactory.getLogger(AbstractBaseTest.class);

    protected TestData testData;

    @Value("${ikea.imc.pam.network.port}")
    private int budgetServicePort;

    @Value("${ikea.imc.pam.budget.service.docker.file.location}")
    private String dockerFileLocation;

    @Value("${ikea.imc.pam.budget.service.docker.container.name}")
    private String budgetServiceContainerName;

    @Value("${ikea.imc.pam.budget.service.docker.standalone:false}")
    private boolean dockerStandalone;

    @Value("${ikea.imc.pam.budget.service.wiremock.host}")
    private String wiremockHost;

    @Value("${ikea.imc.pam.budget.service.wiremock.port}")
    private int wiremockPort;

    private static DockerComposeContainer container;

    @Configuration
    @ComponentScan("ikea.imc.pam.budget.service")
    public static class TestConfig {}

    @PostConstruct
    public void init() {
        log.debug("Application handled by component test: {}", dockerStandalone);

        WireMock.configureFor(wiremockHost, wiremockPort);
        WireMock.reset();
    }

    @BeforeEach
    public void setup() {
        // We share the docker instance between tests to speed up how long it takes to run the suite
        if (!dockerStandalone && container == null) {
            container =
                    new DockerComposeContainer(new File(dockerFileLocation))
                            .withRemoveImages(DockerComposeContainer.RemoveImages.ALL)
                            .withExposedService(budgetServiceContainerName, budgetServicePort, Wait.forHealthcheck());
            container.start();
        }
        testData = new TestData();
    }
}
