package br.com.cielo.ticket.iandt.helper;

import io.restassured.response.Response;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class ReservationEnvHelper {

    public static String reserveTicket(String eventId) {
        var reserveRequest = """
                {
                    "eventId": "%s"
                }
                """.formatted(eventId);

        return given()
                .body(reserveRequest)
        .when()
                .post("/v1/reservations")
        .then()
                .statusCode(202)
                .extract()
                .path("protocolId");
    }

    public static Response getReservation(UUID reservationId) {
        return given()
                .when()
                .get("/v1/reservations/{id}", reservationId);
    }

    public static Response getReservation(String reservationId) {
        return getReservation(UUID.fromString(reservationId));
    }
}
