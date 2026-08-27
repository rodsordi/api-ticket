package br.com.cielo.ticket.infra.adapter;

import br.com.cielo.ticket.domain.entity.Client;
import br.com.cielo.ticket.domain.port.ClientExternalPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class ClientExternalAdapter implements ClientExternalPort {

    @Override
    public Optional<Client> findClientById(UUID clientId) {
        log.info("Fetching client data from external Keycloak service for clientId {}", clientId);
        return Optional.empty();
    }
}
