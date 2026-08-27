package br.com.cielo.ticket.application.v1.def;

import br.com.cielo.ticket.domain.entity.enums.EventStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public interface EventDef {

    interface CreateRequest {

        @Schema(example = "Rock in Rio 2026", description = "Nome do evento.")
        @NotBlank(message = "Name is required")
        @Size(min = 3, max = 150, message = "Name must be between 3 and 150 characters")
        String name();

        @Schema(example = "Festival de Música", description = "Descrição do evento.")
        @Size(max = 1000, message = "Description must not exceed 1000 characters")
        String description();

        @Schema(example = "350.00", description = "Preço do ingresso.")
        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.00", message = "Price cannot be negative")
        @Digits(integer = 8, fraction = 2, message = "Invalid price format")
        BigDecimal price();

        @Schema(example = "2026-10-01T00:00:00", description = "Data e hora de lançamento.")
        @NotNull(message = "Launching date/time is required")
        @FutureOrPresent(message = "Launching date/time must be present or future")
        LocalDateTime launchingDateTime();

        @Schema(example = "2026-12-01", description = "Data do evento.")
        @NotNull(message = "Event date is required")
        @Future(message = "Event date must be in the future")
        LocalDate eventDate();

        @Schema(example = "10000", description = "Quantidade total de ingressos.")
        @Min(value = 1, message = "Total quantity must be at least 1")
        int totalQuantity();
    }

    interface Response {

        @Schema(example = "1029deef-ed87-46c3-b345-a13b30168659", description = "ID do evento.")
        UUID id();

        @Schema(example = "Rock in Rio 2026", description = "Nome do evento.")
        String name();

        @Schema(example = "OPENED_FOR_SALE", description = "Status do evento.")
        EventStatus status();

        @Schema(example = "Festival de Música", description = "Descrição do evento.")
        String description();

        @Schema(example = "350.00", description = "Preço do ingresso.")
        BigDecimal price();

        @Schema(example = "2026-10-01T00:00:00", description = "Data e hora de lançamento.")
        LocalDateTime launchingDateTime();

        @Schema(example = "2026-12-01", description = "Data do evento.")
        LocalDate eventDate();
    }

    interface AvailabilityResponse {

        @Schema(example = "1029deef-ed87-46c3-b345-a13b30168659", description = "ID do evento.")
        UUID eventId();

        @Schema(example = "250", description = "Quantidade de estoque disponível.")
        int availableStock();
    }
}
