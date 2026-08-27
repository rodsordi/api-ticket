package br.com.cielo.ticket.application.use_case;

import br.com.cielo.ticket.TicketIntegrationTest;
import br.com.cielo.ticket.domain.entity.Reservation;
import br.com.cielo.ticket.domain.entity.enums.ReservationStatus;
import br.com.cielo.ticket.domain.repository.ReservationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static br.com.cielo.ticket.domain.entity.factory.ClientFactory.create_Client;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@DisplayName("GetReservationUseCase Application Integration Test Suite")
class GetReservationUseCaseIntegrationTest extends TicketIntegrationTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Nested
    @DisplayName("GET /v1/reservations/{id}")
    class GetReservationEndpoint {

        @Nested
        @DisplayName("Success Scenarios")
        class Success {

            @Test
            @DisplayName("should return reservation via REST Assured and return 200 OK")
            void shouldGetReservationViaRestAssured() {
                var reservationId = UUID.randomUUID();
                var reservation = Reservation.builder()
                        .id(reservationId)
                        .status(ReservationStatus.REQUESTED)
                        .client(create_Client().valid())
                        .build();

                reservationRepository.save(reservation);

                given()
                .when()
                        .get("/v1/reservations/{id}", reservationId)
                .then()
                        .statusCode(200)
                        .body("id", equalTo(reservationId.toString()))
                        .body("status", equalTo("REQUESTED"));
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
