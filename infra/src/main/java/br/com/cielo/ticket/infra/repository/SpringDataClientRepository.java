package br.com.cielo.ticket.infra.repository;

import br.com.cielo.ticket.domain.entity.Client;
import br.com.cielo.ticket.domain.repository.ClientRepository;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataClientRepository extends CassandraRepository<Client, UUID>, ClientRepository {

    @Query("SELECT * FROM clients WHERE email = ?0 ALLOW FILTERING")
    @Override
    Optional<Client> findByEmail(String email);

    @Query("SELECT * FROM clients WHERE document = ?0 ALLOW FILTERING")
    @Override
    Optional<Client> findByDocument(String document);
}
