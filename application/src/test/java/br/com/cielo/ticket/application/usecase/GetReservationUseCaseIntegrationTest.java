package br.com.cielo.ticket.application.usecase;

import br.com.cielo.ticket.TicketIntegrationTest;
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

@DisplayName("GetReservationUseCase Application Integration Test Suite")
class GetReservationUseCaseIntegrationTest extends TicketIntegrationTest {

    @Nested
    @DisplayName("GET /v1/reservations/{id}")
    class GetReservationEndpoint {

        @Nested
        @DisplayName("Success Scenarios")
        class Success {

            @Test
            @DisplayName("should return reservation via REST Assured and return 200 OK")
            void shouldGetReservationViaRestAssured() {
                String eventId = createEvent(100);
                String protocolId = reserveTicket(eventId);
                UUID reservationId = UUID.fromString(protocolId);

                await().atMost(10, SECONDS)
                        .pollInterval(500, MILLISECONDS)
                        .untilAsserted(() ->
                                getReservation(reservationId)
                                .then()
                                        .statusCode(200)
                                        .body("id", equalTo(protocolId))
                                        .body("status", equalTo("AWAITING_PAYMENT"))
                        );
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
