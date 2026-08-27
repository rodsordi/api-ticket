package br.com.cielo.ticket.application.v1.controller;

import br.com.cielo.ticket.application.v1.dto.EventDto;
import br.com.cielo.ticket.application.v1.mapper.EventMapper;
import br.com.cielo.ticket.application.v1.swagger.EventSwagger;
import br.com.cielo.ticket.domain.usecase.CheckEventAvailabilityUseCase;
import br.com.cielo.ticket.domain.usecase.CreateEventUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/v1/events")
@RequiredArgsConstructor
public class EventController implements EventSwagger {

    private final CreateEventUseCase createEventUseCase;
    private final CheckEventAvailabilityUseCase checkEventAvailabilityUseCase;
    private final EventMapper eventMapper;

    @PostMapping
    @Override
    public ResponseEntity<EventDto.Response> createEvent(@Valid @RequestBody EventDto.CreateRequest request) {
        var eventEntity = eventMapper.toEntity(request);
        var createdEvent = createEventUseCase.execute(eventEntity);
        var response = eventMapper.toResponse(createdEvent);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdEvent.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}/availability")
    @Override
    public ResponseEntity<EventDto.AvailabilityResponse> checkAvailability(@PathVariable("id") UUID eventId) {
        int availableStock = checkEventAvailabilityUseCase.execute(eventId);
        var response = EventDto.AvailabilityResponse.builder()
                .eventId(eventId)
                .availableStock(availableStock)
                .build();
        return ResponseEntity.ok(response);
    }
}
