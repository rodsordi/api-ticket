package br.com.cielo.ticket.iandt.usecase;

import br.com.cielo.ticket.iandt.BaseEnvironmentTest;
import br.com.cielo.ticket.iandt.helper.EventEnvHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@DisplayName("CheckEventAvailabilityUseCase Environment Integration Test Suite")
class CheckEventAvailabilityUseCaseEnvIntTest extends BaseEnvironmentTest {

    @Nested
    @DisplayName("GET /v1/events/{id}/availability")
    class CheckAvailabilityEndpoint {

        @Nested
        @DisplayName("Success Scenarios")
        class Success {

            @Test
            @DisplayName("should create event and check availability returning 200 OK")
            void shouldCheckEventAvailability() {
                String eventId = EventEnvHelper.createEvent("Festival de Jazz 2026", 300);

                given()
                .when()
                        .get("/v1/events/{id}/availability", eventId)
                .then()
                        .statusCode(200)
                        .body("eventId", equalTo(eventId))
                        .body("availableStock", equalTo(300));
            }
        }
    }
}
