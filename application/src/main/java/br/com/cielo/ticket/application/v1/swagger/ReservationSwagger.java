package br.com.cielo.ticket.application.v1.swagger;

import br.com.cielo.ticket.application.v1.dto.ReservationDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

@Tag(name = "Reservations (v1)", description = "Gerenciamento e processamento de reservas de ingressos")
@SecurityRequirement(name = "bearerAuth")
public interface ReservationSwagger extends GenericSwagger {

    @Operation(
            summary = "Solicitar reserva de ingresso",
            description = "Submete uma solicitação de reserva de ingresso para o cliente autenticado via Token JWT. A reserva é processada de forma assíncrona com redução atômica de estoque no Redis, publicação de evento de reserva e envio de notificação. Suporta idempotência via cabeçalho 'Idempotency-Key'.",
            parameters = {
                    @Parameter(
                            name = "Idempotency-Key",
                            in = ParameterIn.HEADER,
                            description = "Chave única de idempotência (UUID v4 ou hash) enviada pelo cliente para evitar requisições de reserva duplicadas em caso de retransmissão de rede.",
                            required = false,
                            example = "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d"
                    )
            },
            requestBody = @RequestBody(
                    description = "Dados da solicitação de reserva contendo o ID do evento desejado",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservationDto.ReserveRequest.class))
            )
    )
    @ApiResponse(
            responseCode = "202",
            description = "Solicitação de reserva aceita e enviada para fila de processamento assíncrono. Retorna o ID do protocolo de acompanhamento.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservationDto.RequestResponse.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Payload inválido ou parâmetro 'eventId' ausente",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
    )
    @ApiResponse(
            responseCode = "401",
            description = "Não autenticado / Token JWT ausente ou inválido",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
    )
    @ApiResponse(
            responseCode = "409",
            description = "Ingressos esgotados ou conflito de concorrência no estoque",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
    )
    @ApiResponse(
            responseCode = "429",
            description = "Uma requisição idêntica com a mesma 'Idempotency-Key' já está sendo processada no momento. Tente novamente em alguns instantes.",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
    )
    ResponseEntity<ReservationDto.RequestResponse> reserveTicket(
            @Valid @org.springframework.web.bind.annotation.RequestBody ReservationDto.ReserveRequest request,
            @Parameter(hidden = true) Jwt jwt
    );

    @Operation(
            summary = "Buscar reserva por ID",
            description = "Recupera os detalhes completos de uma reserva cadastrada no sistema, incluindo o status atual do ciclo de vida e a URL do comprovante em PDF no S3."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Reserva localizada com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservationDto.Response.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Reserva não encontrada para o ID fornecido",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
    )
    ResponseEntity<ReservationDto.Response> getReservation(
            @Parameter(
                    name = "id",
                    in = ParameterIn.PATH,
                    description = "ID único da reserva (UUID v4)",
                    required = true,
                    example = "767e1d48-9986-4559-8eed-4db955d9e757"
            ) UUID reservationId
    );

    @Operation(
            summary = "Cancelar reserva",
            description = "Realiza o cancelamento de uma reserva pendente ou confirmada e devolve os ingressos reservados ao saldo do estoque no Redis."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Reserva cancelada com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservationDto.Response.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "A reserva não se encontra em um status elegível para cancelamento (ex: já finalizada ou expirada)",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Reserva não encontrada para o ID fornecido",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
    )
    ResponseEntity<ReservationDto.Response> cancelReservation(
            @Parameter(
                    name = "id",
                    in = ParameterIn.PATH,
                    description = "ID único da reserva a ser cancelada (UUID v4)",
                    required = true,
                    example = "767e1d48-9986-4559-8eed-4db955d9e757"
            ) UUID reservationId
    );
}
