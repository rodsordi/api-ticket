package br.com.cielo.ticket.domain.repository;

import br.com.cielo.ticket.domain.entity.Reservation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository {

    Reservation save(Reservation reservation);

    Optional<Reservation> findById(UUID id);

    List<Reservation> findByClientId(UUID clientId);

    void deleteById(UUID id);
}
