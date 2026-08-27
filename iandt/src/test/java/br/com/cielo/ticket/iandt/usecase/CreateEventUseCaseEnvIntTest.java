package br.com.cielo.ticket.iandt.usecase;

import br.com.cielo.ticket.iandt.BaseEnvironmentTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@DisplayName("CreateEventUseCase Environment Integration Test Suite")
class CreateEventUseCaseEnvIntTest extends BaseEnvironmentTest {

    @Nested
    @DisplayName("POST /v1/events")
    class CreateEventEndpoint {

        @Nested
        @DisplayName("Success Scenarios")
        class Success {

            @Test
            @DisplayName("should create event and return 201 Created")
            void shouldCreateEvent() {
                var requestPayload = """
                        {
                            "name": "Show de Rock 2026",
                            "description": "Festival no Estádio",
                            "price": 250.00,
                            "availableQuantity": 5000,
                            "eventDate": "2026-11-20"
                        }
                        """;

                given()
                        .body(requestPayload)
                .when()
                        .post("/v1/events")
                .then()
                        .statusCode(201)
                        .body("id", notNullValue())
                        .body("name", equalTo("Show de Rock 2026"))
                        .body("price", equalTo(250.00f))
                        .body("availableQuantity", equalTo(5000));
            }
        }
    }
}
