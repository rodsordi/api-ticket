package br.com.cielo.ticket.iandt.usecase;

import br.com.cielo.ticket.iandt.BaseEnvironmentTest;
import br.com.cielo.ticket.iandt.helper.EventEnvHelper;
import br.com.cielo.ticket.iandt.helper.ReservationEnvHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;

@DisplayName("CancelReservationUseCase Environment Integration Test Suite")
class CancelReservationUseCaseEnvIntTest extends BaseEnvironmentTest {

    @Nested
    @DisplayName("POST /v1/reservations/{id}/cancel")
    class CancelReservationEndpoint {

        @Nested
        @DisplayName("Success Scenarios")
        class Success {

            @Test
            @DisplayName("should create event and reservation before canceling reservation")
            void shouldCreateEventAndReservationBeforeCanceling() {
                String eventId = EventEnvHelper.createEvent("Festival de MPB 2026", 200);
                String protocolId = ReservationEnvHelper.reserveTicket(eventId);

                given()
                .when()
                        .post("/v1/reservations/{id}/cancel", protocolId)
                .then();
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
                        .post("/v1/reservations/{id}/cancel", nonExistentId)
                .then()
                        .statusCode(404);
            }
        }
    }
}
