package br.com.cielo.ticket.application.v1.factory;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static br.com.cielo.ticket.application.v1.factory.ReservationDtoFactory.create_ReservationDto;
import static io.restassured.RestAssured.given;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class ReservationIntegrationHelper {

    public static String reserveTicket(String eventId) {
        return reserveTicket(UUID.fromString(eventId));
    }

    public static String reserveTicket(UUID eventId) {
        var request = create_ReservationDto().valid(eventId);

        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(request)
        .when()
                .post("/v1/reservations")
        .then()
                .statusCode(202)
                .extract()
                .path("protocolId");
    }

    public static Response getReservation(UUID reservationId) {
        return given()
                .accept(ContentType.JSON)
        .when()
                .get("/v1/reservations/{id}", reservationId);
    }

    public static Response getReservation(String reservationId) {
        return getReservation(UUID.fromString(reservationId));
    }
}
