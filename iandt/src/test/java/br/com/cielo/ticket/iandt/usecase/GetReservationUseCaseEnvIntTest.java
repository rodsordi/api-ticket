package br.com.cielo.ticket.iandt.usecase;

import br.com.cielo.ticket.iandt.BaseEnvironmentTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@DisplayName("GetReservationUseCase Environment Integration Test Suite")
class GetReservationUseCaseEnvIntTest extends BaseEnvironmentTest {

    @Nested
    @DisplayName("GET /v1/reservations/{id}")
    class GetReservationEndpoint {

        @Nested
        @DisplayName("Success Scenarios")
        class Success {

            @Test
            @DisplayName("should create event and reservation before query")
            void shouldCreateEventAndReservationBeforeQuery() {
                var createEventPayload = """
                        {
                            "name": "Cinema Premium 2026",
                            "description": "Sessão Exclusiva de Cinema",
                            "price": 60.00,
                            "availableQuantity": 100,
                            "eventDate": "2026-11-25"
                        }
                        """;

                String eventId = given()
                        .body(createEventPayload)
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
            @DisplayName("should return 404 Not Found when reservation does not exist")
            void shouldReturn404WhenReservationNotFound() {
                var nonExistentId = UUID.randomUUID();

                given()
                .when()
                        .get("/v1/reservations/{id}", nonExistentId)
                .then()
                        .statusCode(404);
            }
        }
    }
}
