package br.com.cielo.ticket.application.use_case;

import br.com.cielo.ticket.TicketIntegrationTest;
import br.com.cielo.ticket.domain.port.EventAvailabilityCachePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@DisplayName("CheckEventAvailabilityUseCase Application Integration Test Suite")
class CheckEventAvailabilityUseCaseIntegrationTest extends TicketIntegrationTest {

    @Autowired
    private EventAvailabilityCachePort availabilityCachePort;

    @Nested
    @DisplayName("GET /v1/events/{id}/availability")
    class CheckAvailabilityEndpoint {

        @Nested
        @DisplayName("Success Scenarios")
        class Success {

            @Test
            @DisplayName("should return stock count via REST Assured and return 200 OK")
            void shouldReturnStockViaRestAssured() {
                var eventId = UUID.randomUUID();
                availabilityCachePort.initializeStock(eventId, 250);

                given()
                .when()
                        .get("/v1/events/{id}/availability", eventId)
                .then()
                        .statusCode(200)
                        .body("eventId", equalTo(eventId.toString()))
                        .body("availableStock", equalTo(250));
            }
        }
    }
}
