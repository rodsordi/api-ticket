package br.com.cielo.ticket.application.repository;

import br.com.cielo.ticket.domain.entity.Event;
import br.com.cielo.ticket.domain.repository.EventRepository;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EventRepositoryAdapter extends EventRepository, CassandraRepository<Event, UUID> {

}
