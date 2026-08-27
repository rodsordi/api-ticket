package br.com.cielo.ticket.application.v1.swagger;

import br.com.cielo.ticket.application.v1.dto.ReservationDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@Tag(name = "Reservations (v1)", description = "Gerenciamento de reservas de ingressos")
public interface ReservationSwagger extends GenericSwagger {

    @Operation(summary = "Solicitar reserva de ingresso", description = "Registra solicitação de reserva para o cliente autenticado via JWT e retorna o protocolo.")
    @ApiResponse(responseCode = "202", description = "Solicitação de reserva aceita")
    @ApiResponse(responseCode = "400", description = "Ingressos esgotados ou requisição inválida")
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    ResponseEntity<ReservationDto.RequestResponse> reserveTicket(
            @Valid @RequestBody ReservationDto.ReserveRequest request,
            Jwt jwt
    );

    @Operation(summary = "Buscar reserva por ID", description = "Retorna os detalhes de uma reserva existente.")
    @ApiResponse(responseCode = "200", description = "Reserva encontrada")
    @ApiResponse(responseCode = "404", description = "Reserva não encontrada")
    ResponseEntity<ReservationDto.Response> getReservation(
            @Parameter(description = "ID da reserva", example = "767e1d48-9986-4559-8eed-4db955d9e757") UUID reservationId
    );

    @Operation(summary = "Pagar reserva", description = "Altera o status da reserva para PAYED e dispara o evento de conclusão.")
    @ApiResponse(responseCode = "200", description = "Reserva paga com sucesso")
    @ApiResponse(responseCode = "400", description = "Status inválido para pagamento")
    @ApiResponse(responseCode = "404", description = "Reserva não encontrada")
    ResponseEntity<ReservationDto.Response> payReservation(
            @Parameter(description = "ID da reserva", example = "767e1d48-9986-4559-8eed-4db955d9e757") UUID reservationId
    );

    @Operation(summary = "Cancelar reserva", description = "Cancela uma reserva existente.")
    @ApiResponse(responseCode = "200", description = "Reserva cancelada com sucesso")
    @ApiResponse(responseCode = "400", description = "Status inválido para cancelamento")
    @ApiResponse(responseCode = "404", description = "Reserva não encontrada")
    ResponseEntity<ReservationDto.Response> cancelReservation(
            @Parameter(description = "ID da reserva", example = "767e1d48-9986-4559-8eed-4db955d9e757") UUID reservationId
    );
}
