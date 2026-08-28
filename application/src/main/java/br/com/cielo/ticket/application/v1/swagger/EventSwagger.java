package br.com.cielo.ticket.application.v1.swagger;

import br.com.cielo.ticket.application.v1.dto.EventDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ProblemDetail;

import java.util.UUID;

@Tag(name = "Events (v1)", description = "Gerenciamento e consulta de eventos")
public interface EventSwagger extends GenericSwagger {

    @Operation(
            summary = "Criar novo evento",
            description = "Cadastra um novo evento no sistema, persistindo os dados no banco de dados e inicializando a quantidade de ingressos disponíveis no cache Redis para controle concorrente de estoque.",
            requestBody = @RequestBody(
                    description = "Dados para criação do evento (nome, descrição, preço, quantidade disponível e data)",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventDto.CreateRequest.class))
            )
    )
    @ApiResponse(
            responseCode = "201",
            description = "Evento criado com sucesso",
            headers = @Header(name = "Location", description = "URI de acesso ao novo evento cadastrado", schema = @Schema(type = "string", example = "http://localhost:8080/api/v1/events/1029deef-ed87-46c3-b345-a13b30168659")),
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventDto.Response.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Dados da requisição inválidos (ex: preço negativo, data no passado, nome em branco)",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
    )
    @ApiResponse(
            responseCode = "422",
            description = "Regra de negócio violada ao cadastrar o evento",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
    )
    ResponseEntity<EventDto.Response> createEvent(@Valid @org.springframework.web.bind.annotation.RequestBody EventDto.CreateRequest request);

    @Operation(
            summary = "Consultar disponibilidade de ingressos",
            description = "Retorna a quantidade atualizada de ingressos ainda disponíveis para venda referente ao evento informado, consultando o saldo em tempo real do estoque no Redis."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Disponibilidade de estoque consultada com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventDto.AvailabilityResponse.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Evento não encontrado para o ID fornecido",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
    )
    ResponseEntity<EventDto.AvailabilityResponse> checkAvailability(
            @Parameter(
                    name = "id",
                    in = ParameterIn.PATH,
                    description = "ID único do evento (UUID v4)",
                    required = true,
                    example = "1029deef-ed87-46c3-b345-a13b30168659"
            ) UUID eventId
    );
}
