package br.com.cielo.ticket.iandt.usecase;

import br.com.cielo.ticket.iandt.BaseEnvironmentTest;
import br.com.cielo.ticket.iandt.helper.KafkaEnvHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static br.com.cielo.ticket.iandt.helper.EventEnvHelper.createEvent;
import static br.com.cielo.ticket.iandt.helper.ReservationEnvHelper.getReservation;
import static br.com.cielo.ticket.iandt.helper.ReservationEnvHelper.reserveTicket;
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
            @DisplayName("should reserve ticket via REST Assured, publish payment message to Kafka, and verify PAYED status")
            void shouldReserveTicketAndProcessPaymentMessage() {
                String eventId = createEvent("Teatro Musical 2026", 50);
                String protocolId = reserveTicket(eventId);
                UUID reservationId = UUID.fromString(protocolId);

                await().atMost(10, SECONDS)
                        .pollInterval(500, MILLISECONDS)
                        .untilAsserted(() ->
                                getReservation(reservationId)
                                .then()
                                        .statusCode(200)
                                        .body("status", equalTo("AWAITING_PAYMENT"))
                                        .body("invoicePdfUrl", notNullValue())
                        );

                KafkaEnvHelper.sendPaymentMessage(reservationId);

                await().atMost(10, SECONDS)
                        .pollInterval(500, MILLISECONDS)
                        .untilAsserted(() ->
                                getReservation(reservationId)
                                .then()
                                        .statusCode(200)
                                        .body("id", equalTo(protocolId))
                                        .body("status", equalTo("PAYED"))
                        );
            }
        }
    }
}
