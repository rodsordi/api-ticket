package br.com.cielo.ticket.application.use_case;

import br.com.cielo.ticket.TicketIntegrationTest;
import br.com.cielo.ticket.domain.entity.Event;
import br.com.cielo.ticket.domain.entity.enums.ReservationStatus;
import br.com.cielo.ticket.domain.port.EventAvailabilityCachePort;
import br.com.cielo.ticket.domain.repository.EventRepository;
import br.com.cielo.ticket.domain.repository.ReservationRepository;
import br.com.cielo.ticket.domain.usecase.ReserveTicketUseCase;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static br.com.cielo.ticket.application.v1.factory.ReservationDtoFactory.create_ReservationDto;
import static br.com.cielo.ticket.domain.entity.factory.ClientFactory.create_Client;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@DisplayName("ReserveTicketUseCase Application Integration Test Suite")
class ReserveTicketUseCaseIntegrationTest extends TicketIntegrationTest {

    @Autowired
    private EventAvailabilityCachePort availabilityCachePort;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReserveTicketUseCase reserveTicketUseCase;

    @Nested
    @DisplayName("POST /v1/reservations")
    class ReserveTicketEndpoint {

        @Nested
        @DisplayName("Success Scenarios")
        class Success {

            @Test
            @DisplayName("should reserve ticket via REST Assured and verify async processing in database")
            void shouldReserveTicketViaRestAssured() throws InterruptedException {
                var eventId = UUID.randomUUID();
                var client = create_Client().valid();
                var event = Event.builder()
                        .id(eventId)
                        .name("Rock in Rio")
                        .description("Festival")
                        .price(new BigDecimal("100.00"))
                        .availableQuantity(50)
                        .eventDate(LocalDate.now().plusMonths(2))
                        .build();

                eventRepository.save(event);
                availabilityCachePort.initializeStock(eventId, 50);

                var request = create_ReservationDto().valid(eventId);

                String protocolIdStr = given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .body(request)
                .when()
                        .post("/v1/reservations")
                .then()
                        .statusCode(202)
                        .body("protocolId", notNullValue())
                        .extract()
                        .path("protocolId");

                UUID protocolId = UUID.fromString(protocolIdStr);

                // Executa o processamento do evento de reserva
                reserveTicketUseCase.processRequested(protocolId, eventId, client);

                // Aguarda a persistência assíncrona
                Thread.sleep(2000);

                // Consulta no banco de dados o status da reserva para validar o caminho feliz
                var optionalReservation = reservationRepository.findById(protocolId);
                assertThat(optionalReservation).isPresent();
                assertThat(optionalReservation.get().getStatus()).isEqualTo(ReservationStatus.AWAITING_PAYMENT);
            }
        }

        @Nested
        @DisplayName("Failure Scenarios - Field & Request Body Validations")
        class Failure {

            @Test
            @DisplayName("should return 400 Bad Request when eventId is null")
            void shouldReturn400WhenEventIdIsNull() {
                var request = create_ReservationDto().valid(null);

                given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .body(request)
                .when()
                        .post("/v1/reservations")
                .then()
                        .statusCode(400)
                        .body("detail", containsString("eventId"));
            }

            @Test
            @DisplayName("should return 422 Unprocessable Content when event stock is out of stock (0)")
            void shouldReturn422WhenEventIsOutOfStock() {
                var eventId = UUID.randomUUID();
                availabilityCachePort.initializeStock(eventId, 0);

                var request = create_ReservationDto().valid(eventId);

                given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .body(request)
                .when()
                        .post("/v1/reservations")
                .then()
                        .statusCode(422)
                        .body("detail", containsString("Ingressos esgotados"));
            }

            @Test
            @DisplayName("should return 400 Bad Request when request body is malformed JSON")
            void shouldReturn400WhenPayloadIsMalformedJson() {
                given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .body("{\"eventId\": }")
                .when()
                        .post("/v1/reservations")
                .then()
                        .statusCode(400)
                        .body("detail", equalTo("Malformed JSON request payload."));
            }
        }
    }
}
