package br.com.cielo.ticket.iandt.usecase;

import br.com.cielo.ticket.iandt.BaseEnvironmentTest;
import br.com.cielo.ticket.iandt.helper.EventEnvHelper;
import br.com.cielo.ticket.iandt.helper.ReservationEnvHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@DisplayName("GetReservationUseCase Environment Integration Test Suite")
class GetReservationUseCaseEnvIntTest extends BaseEnvironmentTest {

    @Nested
    @DisplayName("GET /v1/reservations/{id}")
    class GetReservationEndpoint {

        @Nested
        @DisplayName("Success Scenarios")
        class Success {

            @Test
            @DisplayName("should create event and reservation before query and then query reservation")
            void shouldCreateEventAndReservationBeforeQuery() {
                String eventId = EventEnvHelper.createEvent("Cinema Premium 2026", 100);
                String protocolId = ReservationEnvHelper.reserveTicket(eventId);

                given()
                .when()
                        .get("/v1/reservations/{id}", protocolId)
                .then();
            }
        }
    }
}
