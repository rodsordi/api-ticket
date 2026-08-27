package br.com.cielo.ticket.iandt.usecase;

import br.com.cielo.ticket.iandt.BaseEnvironmentTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;

@DisplayName("CancelReservationUseCase Environment Integration Test Suite")
class CancelReservationUseCaseEnvIntTest extends BaseEnvironmentTest {

    @Nested
    @DisplayName("POST /v1/reservations/{id}/cancel")
    class CancelReservationEndpoint {

        @Nested
        @DisplayName("Failure Scenarios")
        class Failure {

            @Test
            @DisplayName("should return 404 Not Found when reservation does not exist")
            void shouldReturn404WhenReservationNotFound() {
                var nonExistentId = UUID.randomUUID();

                given()
                .when()
                        .post("/v1/reservations/{id}/cancel", nonExistentId)
                .then()
                        .statusCode(404);
            }
        }
    }
}
