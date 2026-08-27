package br.com.cielo.ticket.domain.usecase;

import br.com.cielo.ticket.domain.port.EventAvailabilityCachePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CheckEventAvailabilityUseCase {

    private final EventAvailabilityCachePort availabilityCachePort;

    public int execute(UUID eventId) {
        return availabilityCachePort.getStock(eventId);
    }
}
