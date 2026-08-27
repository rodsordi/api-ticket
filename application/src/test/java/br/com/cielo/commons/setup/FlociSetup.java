package br.com.cielo.commons.setup;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

public interface FlociSetup {

    LocalStackContainer FLOCI = new LocalStackContainer(DockerImageName.parse("localstack/localstack:3.5.0"))
            .withServices(LocalStackContainer.Service.S3)
            .withEnv("SKIP_SSL_CERT_DOWNLOAD", "true");

    @BeforeAll
    static void beforeAllFloci() {
        FLOCI.start();
    }

    @DynamicPropertySource
    static void configurePropertiesFloci(DynamicPropertyRegistry registry) {
        registry.add("spring.cloud.aws.region.static", FLOCI::getRegion);
        registry.add("spring.cloud.aws.credentials.access-key", FLOCI::getAccessKey);
        registry.add("spring.cloud.aws.credentials.secret-key", FLOCI::getSecretKey);
        registry.add("spring.cloud.aws.s3.endpoint", () -> FLOCI.getEndpointOverride(LocalStackContainer.Service.S3).toString());
        registry.add("spring.cloud.aws.s3.account", () -> "000000000000");
    }
}
