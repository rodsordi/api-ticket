package br.com.cielo.ticket.domain.repository;

import br.com.cielo.ticket.domain.entity.Client;

import java.util.UUID;

public interface ClientExternalRepository {
    Client fetchClient(UUID clientId);
}
