package br.com.cielo.ticket.iandt.usecase;

import br.com.cielo.ticket.iandt.BaseEnvironmentTest;
import br.com.cielo.ticket.iandt.helper.EventEnvHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
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
            @DisplayName("should reserve ticket for existing event and wait until status is AWAITING_PAYMENT")
            void shouldReserveTicket() {
                String eventId = EventEnvHelper.createEvent("Teatro Musical 2026", 1000);

                var reserveRequest = """
                        {
                            "eventId": "%s"
                        }
                        """.formatted(eventId);

                String protocolId = given()
                        .body(reserveRequest)
                .when()
                        .post("/v1/reservations")
                .then()
                        .statusCode(202)
                        .body("protocolId", notNullValue())
                        .extract()
                        .path("protocolId");

                await().atMost(10, SECONDS)
                        .pollInterval(500, MILLISECONDS)
                        .untilAsserted(() ->
                                given()
                                .when()
                                        .get("/v1/reservations/{id}", protocolId)
                                .then()
                                        .statusCode(200)
                                        .body("status", equalTo("AWAITING_PAYMENT"))
                        );
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
