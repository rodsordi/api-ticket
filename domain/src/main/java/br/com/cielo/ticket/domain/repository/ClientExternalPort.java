package br.com.cielo.ticket.domain.repository;

import br.com.cielo.ticket.domain.entity.Client;

import java.util.Optional;
import java.util.UUID;

public interface ClientExternalPort {
    Optional<Client> findClientById(UUID clientId);
}
