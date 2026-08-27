package br.com.cielo.ticket.iandt.helper;

import lombok.NoArgsConstructor;

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
}
