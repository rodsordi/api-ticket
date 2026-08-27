package br.com.cielo.ticket.infra.repository;

import br.com.cielo.ticket.domain.entity.Reservation;
import br.com.cielo.ticket.domain.repository.ReservationRepository;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationRepositoryExt extends ReservationRepository, CassandraRepository<Reservation, UUID> {

    @Query("SELECT * FROM reservations WHERE client.id = ?0 ALLOW FILTERING")
    @Override
    List<Reservation> findByClientId(UUID clientId);
}
