package br.com.cielo.ticket.application.usecase;

import br.com.cielo.ticket.TicketIntegrationTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static br.com.cielo.ticket.application.v1.factory.EventDtoFactory.create_EventDto;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
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
                var request = create_EventDto().valid();

                given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .body(request)
                .when()
                        .post("/v1/events")
                .then()
                        .statusCode(201)
                        .body("id", notNullValue())
                        .body("name", equalTo("Rock in Rio 2026"))
                        .body("price", equalTo(350.00f))
                        .body("availableQuantity", equalTo(10000));
            }
        }

        @Nested
        @DisplayName("Failure Scenarios - Field Validations")
        class Failure {

            @ParameterizedTest
            @ValueSource(strings = {"", "   ", "a", "ab"})
            @DisplayName("should return 400 Bad Request when name is blank or less than 3 characters")
            void shouldReturn400WhenNameIsInvalid(String invalidName) {
                var request = create_EventDto().validBuilder()
                        .name(invalidName)
                        .build();

                given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .body(request)
                .when()
                        .post("/v1/events")
                .then()
                        .statusCode(400)
                        .body("detail", containsString("name"));
            }

            @Test
            @DisplayName("should return 400 Bad Request when name exceeds 150 characters")
            void shouldReturn400WhenNameIsTooLong() {
                var request = create_EventDto().validBuilder()
                        .name("A".repeat(151))
                        .build();

                given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .body(request)
                .when()
                        .post("/v1/events")
                .then()
                        .statusCode(400)
                        .body("detail", containsString("name"));
            }

            @Test
            @DisplayName("should return 400 Bad Request when description exceeds 1000 characters")
            void shouldReturn400WhenDescriptionIsTooLong() {
                var request = create_EventDto().validBuilder()
                        .description("D".repeat(1001))
                        .build();

                given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .body(request)
                .when()
                        .post("/v1/events")
                .then()
                        .statusCode(400)
                        .body("detail", containsString("description"));
            }

            @Test
            @DisplayName("should return 400 Bad Request when price is null")
            void shouldReturn400WhenPriceIsNull() {
                var request = create_EventDto().validBuilder()
                        .price(null)
                        .build();

                given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .body(request)
                .when()
                        .post("/v1/events")
                .then()
                        .statusCode(400)
                        .body("detail", containsString("price"));
            }

            @Test
            @DisplayName("should return 400 Bad Request when price is negative")
            void shouldReturn400WhenPriceIsNegative() {
                var request = create_EventDto().validBuilder()
                        .price(new BigDecimal("-50.00"))
                        .build();

                given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .body(request)
                .when()
                        .post("/v1/events")
                .then()
                        .statusCode(400)
                        .body("detail", containsString("price"));
            }

            @Test
            @DisplayName("should return 400 Bad Request when availableQuantity is null")
            void shouldReturn400WhenAvailableQuantityIsNull() {
                var request = create_EventDto().validBuilder()
                        .availableQuantity(null)
                        .build();

                given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .body(request)
                .when()
                        .post("/v1/events")
                .then()
                        .statusCode(400)
                        .body("detail", containsString("availableQuantity"));
            }

            @Test
            @DisplayName("should return 400 Bad Request when eventDate is in the past")
            void shouldReturn400WhenEventDateIsInThePast() {
                var request = create_EventDto().validBuilder()
                        .eventDate(LocalDate.now().minusDays(1))
                        .build();

                given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .body(request)
                .when()
                        .post("/v1/events")
                .then()
                        .statusCode(400)
                        .body("detail", containsString("eventDate"));
            }

            @Test
            @DisplayName("should return 400 Bad Request when payload is malformed JSON")
            void shouldReturn400WhenPayloadIsMalformedJson() {
                var malformedJson = """
                        {"name": "Invalid Json}
                        """;

                given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .body(malformedJson)
                .when()
                        .post("/v1/events")
                .then()
                        .statusCode(400)
                        .body("detail", equalTo("Malformed JSON request payload."));
            }
        }
    }
}
