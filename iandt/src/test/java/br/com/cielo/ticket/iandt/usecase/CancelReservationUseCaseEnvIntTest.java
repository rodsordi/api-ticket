package br.com.cielo.ticket.iandt.usecase;

import br.com.cielo.ticket.iandt.BaseEnvironmentTest;
import br.com.cielo.ticket.iandt.helper.EventEnvHelper;
import br.com.cielo.ticket.iandt.helper.ReservationEnvHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;

@DisplayName("CancelReservationUseCase Environment Integration Test Suite")
class CancelReservationUseCaseEnvIntTest extends BaseEnvironmentTest {

    @Nested
    @DisplayName("POST /v1/reservations/{id}/cancel")
    class CancelReservationEndpoint {

        @Nested
        @DisplayName("Success Scenarios")
        class Success {

            @Test
            @DisplayName("should cancel reservation via REST Assured and return 200 OK")
            void shouldCancelReservationViaRestAssured() {
                String eventId = EventEnvHelper.createEvent("Festival de MPB 2026", 200);
                String protocolId = ReservationEnvHelper.reserveTicket(eventId);
                UUID reservationId = UUID.fromString(protocolId);

                await().atMost(10, SECONDS)
                        .pollInterval(500, MILLISECONDS)
                        .untilAsserted(() ->
                                ReservationEnvHelper.getReservation(reservationId)
                                .then()
                                        .statusCode(200)
                                        .body("status", equalTo("AWAITING_PAYMENT"))
                        );

                given()
                .when()
                        .post("/v1/reservations/{id}/cancel", reservationId)
                .then()
                        .statusCode(200)
                        .body("id", equalTo(protocolId))
                        .body("status", equalTo("CANCELED"));
            }
        }
    }
}
