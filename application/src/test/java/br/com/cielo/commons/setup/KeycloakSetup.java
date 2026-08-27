package br.com.cielo.commons.setup;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.MountableFile;

public interface KeycloakSetup {

    GenericContainer<?> KEYCLOAK = new GenericContainer<>("quay.io/keycloak/keycloak:26.0.0")
            .withExposedPorts(8080)
            .withEnv("KEYCLOAK_ADMIN", "admin")
            .withEnv("KEYCLOAK_ADMIN_PASSWORD", "admin")
            .withCopyFileToContainer(
                    MountableFile.forClasspathResource("keycloak-realm.json"),
                    "/opt/keycloak/data/import/keycloak-realm.json"
            )
            .withCommand("start-dev --import-realm")
            .waitingFor(Wait.forHttp("/realms/ticket-realm").forPort(8080).forStatusCode(200));

    @BeforeAll
    static void beforeAllKeycloak() {
        KEYCLOAK.start();
    }

    @DynamicPropertySource
    static void configurePropertiesKeycloak(DynamicPropertyRegistry registry) {
        String authServerUrl = "http://" + KEYCLOAK.getHost() + ":" + KEYCLOAK.getMappedPort(8080);
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> authServerUrl + "/realms/ticket-realm");
        registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri",
                () -> authServerUrl + "/realms/ticket-realm/protocol/openid-connect/certs");
    }

    static String getAccessToken() {
        String authServerUrl = "http://" + KEYCLOAK.getHost() + ":" + KEYCLOAK.getMappedPort(8080);
        return RestAssured.given()
                .spec(new RequestSpecBuilder().build())
                .baseUri(authServerUrl)
                .contentType("application/x-www-form-urlencoded")
                .formParam("grant_type", "password")
                .formParam("client_id", "ticket-client")
                .formParam("username", "test-user")
                .formParam("password", "test-password")
                .post("/realms/ticket-realm/protocol/openid-connect/token")
                .then()
                .statusCode(200)
                .extract()
                .path("access_token");
    }
}
