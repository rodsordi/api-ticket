package br.com.cielo.ticket.iandt;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class BaseEnvironmentTest {

    protected static String domainUrl;

    @BeforeAll
    static void setupEnvironment() {
        Properties properties = new Properties();
        try (InputStream input = BaseEnvironmentTest.class.getClassLoader().getResourceAsStream("environment.properties")) {
            if (input == null) {
                throw new IllegalStateException("Arquivo environment.properties não foi encontrado no classpath.");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Falha ao carregar o arquivo environment.properties", e);
        }

        domainUrl = System.getProperty("api.domain.url", properties.getProperty("api.domain.url", "http://localhost:8080/api"));
        String keycloakUrl = System.getProperty("keycloak.auth.url", properties.getProperty("keycloak.auth.url", "http://localhost:8081"));
        String realm = properties.getProperty("keycloak.realm", "ticket");
        String clientId = properties.getProperty("keycloak.client-id", "api-ticket-backend");
        String username = properties.getProperty("keycloak.username", "admin@ticket.com");
        String password = properties.getProperty("keycloak.password", "admin123");

        String token = obtainAccessToken(keycloakUrl, realm, clientId, username, password);

        RestAssured.reset();
        RestAssured.defaultParser = Parser.JSON;
        RestAssured.registerParser("application/problem+json", Parser.JSON);
        RestAssured.baseURI = domainUrl;
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addHeader("Authorization", "Bearer " + token)
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .build();
    }

    private static String obtainAccessToken(String keycloakUrl, String realm, String clientId, String username, String password) {
        int maxRetries = 5;
        for (int i = 0; i < maxRetries; i++) {
            try {
                return RestAssured.given()
                        .baseUri(keycloakUrl)
                        .contentType("application/x-www-form-urlencoded")
                        .formParam("grant_type", "password")
                        .formParam("client_id", clientId)
                        .formParam("username", username)
                        .formParam("password", password)
                        .post("/realms/" + realm + "/protocol/openid-connect/token")
                        .then()
                        .statusCode(200)
                        .extract()
                        .path("access_token");
            } catch (Exception e) {
                if (i == maxRetries - 1) {
                    System.err.println("Failed to obtain Keycloak token after retries: " + e.getMessage());
                    return "mock-token";
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        return "mock-token";
    }
}
