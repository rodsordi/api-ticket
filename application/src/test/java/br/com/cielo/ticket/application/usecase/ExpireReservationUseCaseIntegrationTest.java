package br.com.cielo.ticket.application.usecase;

import br.com.cielo.ticket.TicketIntegrationTest;
import br.com.cielo.ticket.domain.entity.Reservation;
import br.com.cielo.ticket.domain.entity.enums.ReservationStatus;
import br.com.cielo.ticket.domain.port.EventAvailabilityCachePort;
import br.com.cielo.ticket.domain.repository.ReservationRepository;
import br.com.cielo.ticket.domain.usecase.ExpireReservationUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static br.com.cielo.ticket.domain.entity.factory.ClientFactory.create_Client;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@DisplayName("ExpireReservationUseCase Application Integration Test Suite")
class ExpireReservationUseCaseIntegrationTest extends TicketIntegrationTest {

    @Autowired
    private ExpireReservationUseCase expireReservationUseCase;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private EventAvailabilityCachePort availabilityCachePort;

    @Nested
    @DisplayName("execute() method")
    class Execute {

        @Nested
        @DisplayName("Success Scenarios")
        class Success {

            @Test
            @DisplayName("should expire pending reservation, restore stock in Redis and verify via REST Assured")
            void shouldExpireReservationAndRestoreStock() {
                var reservationId = UUID.randomUUID();
                var eventId = UUID.randomUUID();
                var reservation = Reservation.builder()
                        .id(reservationId)
                        .status(ReservationStatus.AWAITING_PAYMENT)
                        .client(create_Client().valid())
                        .build();

                reservationRepository.save(reservation);
                availabilityCachePort.initializeStock(eventId, 5);

                expireReservationUseCase.execute(reservationId, eventId);

                given()
                .when()
                        .get("/v1/reservations/{id}", reservationId)
                .then()
                        .statusCode(200)
                        .body("id", equalTo(reservationId.toString()))
                        .body("status", equalTo("EXPIRED"));

                given()
                .when()
                        .get("/v1/events/{id}/availability", eventId)
                .then()
                        .statusCode(200)
                        .body("eventId", equalTo(eventId.toString()))
                        .body("availableStock", equalTo(6));
            }
        }
    }
}
