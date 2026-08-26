package br.com.cielo.ticket.domain.repository;

import br.com.cielo.ticket.domain.entity.Client;

import java.util.Optional;
import java.util.UUID;

public interface ClientRepository {

    Client save(Client client);

    Optional<Client> findById(UUID id);

    Optional<Client> findByEmail(String email);

    Optional<Client> findByDocument(String document);

    void deleteById(UUID id);
}
