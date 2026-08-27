package br.com.cielo.ticket.iandt.helper;

import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class EventEnvHelper {

    public static String createEvent() {
        return createEvent("Evento de Teste 2026", 1000);
    }

    public static String createEvent(int availableQuantity) {
        return createEvent("Evento de Teste 2026", availableQuantity);
    }

    public static String createEvent(String name, int availableQuantity) {
        var createEventRequest = """
                {
                    "name": "%s",
                    "description": "Descrição do Evento de Teste",
                    "price": 150.00,
                    "availableQuantity": %d,
                    "eventDate": "%s"
                }
                """.formatted(name, availableQuantity, LocalDate.now().plusMonths(3));

        return given()
                .body(createEventRequest)
        .when()
                .post("/v1/events")
        .then()
                .statusCode(201)
                .extract()
                .path("id");
    }
}
