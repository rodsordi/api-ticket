package br.com.cielo.ticket.application.use_case;

import br.com.cielo.ticket.TicketIntegrationTest;
import br.com.cielo.ticket.application.v1.dto.ReservationDto;
import br.com.cielo.ticket.domain.port.EventAvailabilityCachePort;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@DisplayName("ReserveTicketUseCase Application Integration Test Suite")
class ReserveTicketUseCaseIntegrationTest extends TicketIntegrationTest {

    @Autowired
    private EventAvailabilityCachePort availabilityCachePort;

    @Nested
    @DisplayName("POST /v1/reservations")
    class ReserveTicketEndpoint {

        @Nested
        @DisplayName("Success Scenarios")
        class Success {

            @Test
            @DisplayName("should reserve ticket via REST Assured and return 202 Accepted with protocolId")
            void shouldReserveTicketViaRestAssured() {
                var eventId = UUID.randomUUID();
                availabilityCachePort.initializeStock(eventId, 50);

                var request = ReservationDto.ReserveRequest.builder()
                        .eventId(eventId)
                        .build();

                given()
                        .contentType(ContentType.JSON)
                        .body(request)
                .when()
                        .post("/v1/reservations")
                .then()
                        .statusCode(202)
                        .body("protocolId", notNullValue());
            }
        }
    }
}
