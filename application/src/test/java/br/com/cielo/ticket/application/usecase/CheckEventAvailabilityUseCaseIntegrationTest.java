package br.com.cielo.ticket.application.usecase;

import br.com.cielo.ticket.TicketIntegrationTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static br.com.cielo.ticket.application.v1.factory.EventDtoFactory.create_EventDto;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@DisplayName("CheckEventAvailabilityUseCase Application Integration Test Suite")
class CheckEventAvailabilityUseCaseIntegrationTest extends TicketIntegrationTest {

    @Nested
    @DisplayName("GET /v1/events/{id}/availability")
    class CheckAvailabilityEndpoint {

        @Nested
        @DisplayName("Success Scenarios")
        class Success {

            @Test
            @DisplayName("should return stock count via REST Assured and return 200 OK")
            void shouldReturnStockViaRestAssured() {
                var createRequest = create_EventDto().validBuilder()
                        .availableQuantity(250)
                        .build();

                String eventId = given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .body(createRequest)
                .when()
                        .post("/v1/events")
                .then()
                        .statusCode(201)
                        .extract()
                        .path("id");

                given()
                        .accept(ContentType.JSON)
                .when()
                        .get("/v1/events/{id}/availability", eventId)
                .then()
                        .statusCode(200)
                        .body("eventId", equalTo(eventId))
                        .body("availableStock", equalTo(250));
            }
        }
    }
}
