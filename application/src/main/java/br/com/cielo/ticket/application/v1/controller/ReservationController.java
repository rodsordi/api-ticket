package br.com.cielo.ticket.application.v1.controller;

import br.com.cielo.ticket.application.v1.dto.ReservationDto;
import br.com.cielo.ticket.application.v1.mapper.ReservationMapper;
import br.com.cielo.ticket.application.v1.swagger.ReservationSwagger;
import br.com.cielo.ticket.domain.usecase.CancelReservationUseCase;
import br.com.cielo.ticket.domain.usecase.GetReservationUseCase;
import br.com.cielo.ticket.domain.usecase.PayReservationUseCase;
import br.com.cielo.ticket.domain.usecase.ReserveTicketUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/v1/reservations")
@RequiredArgsConstructor
public class ReservationController implements ReservationSwagger {

    private final ReserveTicketUseCase reserveTicketUseCase;
    private final GetReservationUseCase getReservationUseCase;
    private final PayReservationUseCase payReservationUseCase;
    private final CancelReservationUseCase cancelReservationUseCase;
    private final ReservationMapper reservationMapper;

    @PostMapping
    @Override
    public ResponseEntity<ReservationDto.RequestResponse> reserveTicket(
            @Valid @RequestBody ReservationDto.ReserveRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        var clientDomain = reservationMapper.toDomain(jwt);
        var protocolId = reserveTicketUseCase.request(request.eventId(), clientDomain);
        var response = new ReservationDto.RequestResponse(protocolId);
        return ResponseEntity.accepted().body(response);
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<ReservationDto.Response> getReservation(@PathVariable("id") UUID reservationId) {
        var reservation = getReservationUseCase.execute(reservationId);
        var response = reservationMapper.toResponse(reservation);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/pay")
    @Override
    public ResponseEntity<ReservationDto.Response> payReservation(@PathVariable("id") UUID reservationId) {
        var reservation = payReservationUseCase.execute(reservationId);
        var response = reservationMapper.toResponse(reservation);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/cancel")
    @Override
    public ResponseEntity<ReservationDto.Response> cancelReservation(@PathVariable("id") UUID reservationId) {
        var reservation = cancelReservationUseCase.execute(reservationId);
        var response = reservationMapper.toResponse(reservation);
        return ResponseEntity.ok(response);
    }
}
