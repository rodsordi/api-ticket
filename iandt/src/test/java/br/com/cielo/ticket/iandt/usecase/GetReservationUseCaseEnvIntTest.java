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
            @DisplayName("should create event and reservation before query and then query reservation")
            void shouldCreateEventAndReservationBeforeQuery() {
                String eventId = EventEnvHelper.createEvent("Cinema Premium 2026", 100);
                String protocolId = ReservationEnvHelper.reserveTicket(eventId);
                UUID reservationId = UUID.fromString(protocolId);

                await().atMost(10, SECONDS)
                        .pollInterval(500, MILLISECONDS)
                        .untilAsserted(() ->
                                given()
                                .when()
                                        .get("/v1/reservations/{id}", reservationId)
                                .then()
                                        .statusCode(200)
                                        .body("id", equalTo(protocolId))
                                        .body("status", equalTo("AWAITING_PAYMENT"))
                                        .body("invoicePdfUrl", notNullValue())
                        );
            }
        }
    }
}
