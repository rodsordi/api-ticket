package br.com.cielo.ticket.infra.repository;

import br.com.cielo.ticket.domain.entity.Event;
import br.com.cielo.ticket.domain.repository.EventRepository;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataEventRepository extends CassandraRepository<Event, UUID>, EventRepository {

    @Query("SELECT * FROM events WHERE status = ?0 ALLOW FILTERING")
    @Override
    List<Event> findByStatus(Event.Status status);
}
