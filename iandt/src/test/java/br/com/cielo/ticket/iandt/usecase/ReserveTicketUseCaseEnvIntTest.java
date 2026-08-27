package br.com.cielo.ticket.iandt.usecase;

import br.com.cielo.ticket.iandt.BaseEnvironmentTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@DisplayName("ReserveTicketUseCase Environment Integration Test Suite")
class ReserveTicketUseCaseEnvIntTest extends BaseEnvironmentTest {

    @Nested
    @DisplayName("POST /v1/reservations")
    class ReserveTicketEndpoint {

        @Nested
        @DisplayName("Success Scenarios")
        class Success {

            @Test
            @DisplayName("should reserve ticket for existing event and return 202 Accepted with protocolId")
            void shouldReserveTicket() {
                var createEventRequest = """
                        {
                            "name": "Teatro Musical 2026",
                            "description": "Apresentação Teatral",
                            "price": 120.00,
                            "availableQuantity": 1000,
                            "eventDate": "2026-12-15"
                        }
                        """;

                String eventId = given()
                        .body(createEventRequest)
                .when()
                        .post("/v1/events")
                .then()
                        .statusCode(201)
                        .extract()
                        .path("id");

                var reserveRequest = """
                        {
                            "eventId": "%s"
                        }
                        """.formatted(eventId);

                given()
                        .body(reserveRequest)
                .when()
                        .post("/v1/reservations")
                .then()
                        .statusCode(202)
                        .body("protocolId", notNullValue());
            }
        }

        @Nested
        @DisplayName("Failure Scenarios")
        class Failure {

            @Test
            @DisplayName("should return 400 Bad Request when request body is malformed JSON")
            void shouldReturn400WhenPayloadIsMalformedJson() {
                var malformedJson = """
                        {"eventId": }
                        """;

                given()
                        .body(malformedJson)
                .when()
                        .post("/v1/reservations")
                .then()
                        .statusCode(400)
                        .body("detail", equalTo("Malformed JSON request payload."));
            }
        }
    }
}
