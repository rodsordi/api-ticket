package br.com.cielo.ticket.application.usecase;

import br.com.cielo.ticket.TicketIntegrationTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static br.com.cielo.ticket.application.v1.factory.EventIntegrationHelper.createEvent;
import static br.com.cielo.ticket.application.v1.factory.ReservationIntegrationHelper.getReservation;
import static br.com.cielo.ticket.application.v1.factory.ReservationIntegrationHelper.reserveTicket;
import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;

@DisplayName("CancelReservationUseCase Application Integration Test Suite")
class CancelReservationUseCaseIntegrationTest extends TicketIntegrationTest {

    @Nested
    @DisplayName("POST /v1/reservations/{id}/cancel")
    class CancelReservationEndpoint {

        @Nested
        @DisplayName("Success Scenarios")
        class Success {

            @Test
            @DisplayName("should cancel reservation via REST Assured and return 200 OK")
            void shouldCancelReservationViaRestAssured() {
                String eventId = createEvent(100);
                String protocolId = reserveTicket(eventId);
                UUID reservationId = UUID.fromString(protocolId);

                await().atMost(10, SECONDS)
                        .pollInterval(500, MILLISECONDS)
                        .untilAsserted(() ->
                                getReservation(reservationId)
                                .then()
                                        .statusCode(200)
                                        .body("status", equalTo("AWAITING_PAYMENT"))
                        );

                given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                .when()
                        .post("/v1/reservations/{id}/cancel", reservationId)
                .then()
                        .statusCode(200)
                        .body("id", equalTo(protocolId))
                        .body("status", equalTo("CANCELED"));
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
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                .when()
                        .post("/v1/reservations/{id}/cancel", nonExistentId)
                .then()
                        .statusCode(404);
            }
        }
    }
}
