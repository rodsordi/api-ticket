package br.com.cielo.ticket.domain.entity.factory;

import br.com.cielo.ticket.domain.entity.Client;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

import static java.util.UUID.fromString;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class ClientFactory {

    private final Client.ClientBuilder<?, ?> builder;

    public static ClientFactory create_Client() {
        return new ClientFactory(Client.builder());
    }

    public Client withAllFields() {
        return builder
                .id(fromString("0caad1ab-8767-491d-a8c2-f94b50e6f977"))
                .fullName("John Doe")
                .document("12345678901")
                .email("john.doe@cielo.com.br")
                .birthDate(LocalDate.now().minusYears(25))
                .build();
    }

    public Client valid() {
        return builder
                .id(UUID.randomUUID())
                .fullName("Jane Smith")
                .document("98765432100")
                .email("jane.smith@cielo.com.br")
                .birthDate(LocalDate.now().minusYears(30))
                .build();
    }

    public Client initiatedEmpty() {
        return builder.build();
    }
}
