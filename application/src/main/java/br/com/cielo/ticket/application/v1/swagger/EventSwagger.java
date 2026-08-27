package br.com.cielo.ticket.application.v1.swagger;

import br.com.cielo.ticket.application.v1.dto.EventDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "Events (v1)", description = "Gerenciamento e consulta de eventos")
public interface EventSwagger {

    @Operation(summary = "Criar novo evento", description = "Cria um novo evento e inicializa o estoque de ingressos no Redis.")
    @ApiResponse(responseCode = "201", description = "Evento criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos")
    ResponseEntity<EventDto.Response> createEvent(EventDto.CreateRequest request);

    @Operation(summary = "Consultar disponibilidade de ingressos", description = "Retorna o saldo de estoque disponível para o evento informado.")
    @ApiResponse(responseCode = "200", description = "Estoque consultado com sucesso")
    @ApiResponse(responseCode = "404", description = "Evento não encontrado")
    ResponseEntity<EventDto.AvailabilityResponse> checkAvailability(
            @Parameter(description = "ID do evento", example = "1029deef-ed87-46c3-b345-a13b30168659") UUID eventId
    );
}
