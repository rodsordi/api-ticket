package br.com.cielo.ticket.application.usecase;

import br.com.cielo.ticket.TicketIntegrationTest;
import br.com.cielo.ticket.application.v1.msg.NotificationMsg;
import br.com.cielo.ticket.domain.usecase.PayReservationUseCase;
import br.com.cielo.ticket.domain.usecase.ReserveTicketUseCase;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;

import static br.com.cielo.ticket.application.v1.factory.EventIntegrationHelper.createEvent;
import static br.com.cielo.ticket.application.v1.factory.ReservationDtoFactory.create_ReservationDto;
import static br.com.cielo.ticket.application.v1.factory.ReservationIntegrationHelper.getReservation;
import static br.com.cielo.ticket.application.v1.factory.ReservationIntegrationHelper.reserveTicket;
import static br.com.cielo.ticket.domain.entity.factory.ClientFactory.create_Client;
import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@DisplayName("ReserveTicketUseCase Application Integration Test Suite")
class ReserveTicketUseCaseIntegrationTest extends TicketIntegrationTest {

    @Autowired
    private ReserveTicketUseCase reserveTicketUseCase;

    @Autowired
    private PayReservationUseCase payReservationUseCase;

    @Autowired
    private KafkaTemplate<String, NotificationMsg> kafkaTemplate;

    @Nested
    @DisplayName("POST /v1/reservations")
    class ReserveTicketEndpoint {

        @Nested
        @DisplayName("Success Scenarios")
        class Success {

            @Test
            @DisplayName("should reserve ticket via REST Assured, publish payment message to Kafka, and verify processing")
            void shouldReserveTicketAndProcessPaymentMessage() {
                String eventId = createEvent(50);
                String protocolId = reserveTicket(eventId);
                UUID reservationId = UUID.fromString(protocolId);

                reserveTicketUseCase.processRequested(reservationId, UUID.fromString(eventId), create_Client().valid());

                await().atMost(10, SECONDS)
                        .pollInterval(500, MILLISECONDS)
                        .untilAsserted(() ->
                                getReservation(reservationId)
                                .then()
                                        .statusCode(200)
                                        .body("status", equalTo("AWAITING_PAYMENT"))
                                        .body("invoicePdfUrl", notNullValue())
                        );

                var paymentNotificationMsg = NotificationMsg.builder()
                        .externalId(reservationId)
                        .build();

                kafkaTemplate.send("api-ticket_notification-creation_topic", protocolId, paymentNotificationMsg);

                payReservationUseCase.execute(reservationId);

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
                String eventId = createEvent(0);
                var request = create_ReservationDto().valid(UUID.fromString(eventId));

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
                var malformedJson = """
                        {"eventId": }
                        """;

                given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .body(malformedJson)
                .when()
                        .post("/v1/reservations")
                .then()
                        .statusCode(400)
                        .body("detail", equalTo("Malformed JSON request payload."));
            }
        }
    }
}
