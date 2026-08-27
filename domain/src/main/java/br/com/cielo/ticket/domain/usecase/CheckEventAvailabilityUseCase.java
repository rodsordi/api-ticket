package br.com.cielo.ticket.domain.usecase;

import br.com.cielo.ticket.domain.repository.EventAvailabilityCacheRepository;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class CheckEventAvailabilityUseCase {

    private final EventAvailabilityCacheRepository availabilityCacheRepository;

    public boolean execute(UUID eventId) {
        return availabilityCacheRepository.isAvailable(eventId);
    }
}
