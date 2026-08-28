package br.com.cielo.ticket.application.v1.dto;

import br.com.cielo.ticket.application.v1.def.EventDef;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@Schema(description = "DTOs para operações com eventos de ingressos")
public final class EventDto {

    @Builder
    @Schema(description = "Payload de requisição para cadastro de um novo evento")
    public record CreateRequest(
            @Schema(
                    description = "Nome do evento",
                    example = "Rock in Rio 2026",
                    minLength = 3,
                    maxLength = 150,
                    requiredMode = Schema.RequiredMode.REQUIRED
            )
            @NotBlank(message = "Name is required")
            @Size(min = 3, max = 150, message = "Name must be between 3 and 150 characters")
            String name,

            @Schema(
                    description = "Descrição detalhada sobre o evento e suas atrações",
                    example = "Festival internacional de música com múltiplos palcos e atrações ao vivo.",
                    maxLength = 1000
            )
            @Size(max = 1000, message = "Description must not exceed 1000 characters")
            String description,

            @Schema(
                    description = "Preço unitário do ingresso em BRL (Reais)",
                    example = "450.00",
                    minimum = "0.00",
                    requiredMode = Schema.RequiredMode.REQUIRED
            )
            @NotNull(message = "Price is required")
            @DecimalMin(value = "0.00", message = "Price cannot be negative")
            @Digits(integer = 8, fraction = 2, message = "Invalid price format")
            BigDecimal price,

            @Schema(
                    description = "Quantidade total de ingressos disponibilizados para o evento",
                    example = "5000",
                    minimum = "1",
                    requiredMode = Schema.RequiredMode.REQUIRED
            )
            @NotNull(message = "Available quantity is required")
            @Min(value = 1, message = "Available quantity must be at least 1")
            Integer availableQuantity,

            @Schema(
                    description = "Data prevista para realização do evento (deve ser no futuro, no formato AAAA-MM-DD)",
                    example = "2026-12-31",
                    format = "date",
                    requiredMode = Schema.RequiredMode.REQUIRED
            )
            @NotNull(message = "Event date is required")
            @Future(message = "Event date must be in the future")
            LocalDate eventDate
    ) implements EventDef.CreateRequest {
    }

    @Builder
    @Schema(description = "Representação detalhada do evento cadastrado")
    public record Response(
            @Schema(description = "Identificador único do evento (UUID v4)", example = "1029deef-ed87-46c3-b345-a13b30168659")
            UUID id,

            @Schema(description = "Nome do evento", example = "Rock in Rio 2026")
            String name,

            @Schema(description = "Descrição detalhada do evento", example = "Festival internacional de música com múltiplos palcos e atrações ao vivo.")
            String description,

            @Schema(description = "Preço unitário do ingresso em BRL", example = "450.00")
            BigDecimal price,

            @Schema(description = "Quantidade total de ingressos cadastrados", example = "5000")
            Integer availableQuantity,

            @Schema(description = "Data do evento", example = "2026-12-31")
            LocalDate eventDate
    ) implements EventDef.Response {
    }

    @Builder
    @Schema(description = "Resposta da consulta de disponibilidade de saldo de ingressos")
    public record AvailabilityResponse(
            @Schema(description = "Identificador único do evento (UUID v4)", example = "1029deef-ed87-46c3-b345-a13b30168659")
            UUID eventId,

            @Schema(description = "Quantidade de ingressos ainda disponíveis para venda no estoque em tempo real (Redis)", example = "4995")
            int availableStock
    ) implements EventDef.AvailabilityResponse {
    }
}
