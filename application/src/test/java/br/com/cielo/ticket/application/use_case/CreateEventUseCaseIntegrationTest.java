package br.com.cielo.ticket.application.use_case;

import br.com.cielo.ticket.TicketIntegrationTest;
import br.com.cielo.ticket.application.v1.dto.EventDto;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@DisplayName("CreateEventUseCase Application Integration Test Suite")
class CreateEventUseCaseIntegrationTest extends TicketIntegrationTest {

    @Nested
    @DisplayName("POST /v1/events")
    class CreateEventEndpoint {

        @Nested
        @DisplayName("Success Scenarios")
        class Success {

            @Test
            @DisplayName("should create event via REST Assured and return 201 Created")
            void shouldCreateEventViaRestAssured() {
                var request = EventDto.CreateRequest.builder()
                        .name("Rock in Rio 2026")
                        .description("Music Festival")
                        .price(new BigDecimal("350.00"))
                        .launchingDateTime(LocalDateTime.now().plusDays(1))
                        .eventDate(LocalDate.now().plusMonths(2))
                        .totalQuantity(10000)
                        .build();

                given()
                        .contentType(ContentType.JSON)
                        .body(request)
                .when()
                        .post("/v1/events")
                .then()
                        .statusCode(201)
                        .body("id", notNullValue())
                        .body("name", equalTo("Rock in Rio 2026"))
                        .body("price", equalTo(350.00f));
            }
        }
    }
}
