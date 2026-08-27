package br.com.cielo.ticket.application.usecase;

import br.com.cielo.ticket.TicketIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

import java.util.UUID;

import static br.com.cielo.ticket.application.v1.factory.EventIntegrationHelper.createEvent;
import static br.com.cielo.ticket.application.v1.factory.ReservationIntegrationHelper.getReservation;
import static br.com.cielo.ticket.application.v1.factory.ReservationIntegrationHelper.reserveTicket;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;

@TestPropertySource(properties = "reservation.expiration-delay-seconds=5")
@DisplayName("ExpireReservationUseCase Application Integration Test Suite")
class ExpireReservationUseCaseIntegrationTest extends TicketIntegrationTest {

    @Nested
    @DisplayName("execute() method")
    class Execute {

        @Nested
        @DisplayName("Success Scenarios")
        class Success {

            @Test
            @DisplayName("should create event, reserve ticket, wait until AWAITING_PAYMENT, then wait until EXPIRED")
            void shouldExpireReservationAfterExpirationDelay() {
                String eventId = createEvent(10);
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

                await().atMost(12, SECONDS)
                        .pollInterval(1, SECONDS)
                        .untilAsserted(() ->
                                getReservation(reservationId)
                                .then()
                                        .statusCode(200)
                                        .body("status", equalTo("EXPIRED"))
                        );
            }
        }
    }
}
