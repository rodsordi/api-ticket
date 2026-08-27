package br.com.cielo.ticket.application.v1.factory;

import io.restassured.http.ContentType;
import lombok.NoArgsConstructor;

import static br.com.cielo.ticket.application.v1.factory.EventDtoFactory.create_EventDto;
import static io.restassured.RestAssured.given;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class EventIntegrationHelper {

    public static String createEvent(int availableQuantity) {
        var createRequest = create_EventDto().validBuilder()
                .availableQuantity(availableQuantity)
                .build();

        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(createRequest)
        .when()
                .post("/v1/events")
        .then()
                .statusCode(201)
                .extract()
                .path("id");
    }
}
