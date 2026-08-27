package br.com.cielo.ticket.application.v1.dto;

import br.com.cielo.ticket.application.v1.def.EventDef;
import br.com.cielo.ticket.domain.entity.enums.EventStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class EventDto {

    @Builder
    public record CreateRequest(
            @NotBlank(message = "Name is required")
            @Size(min = 3, max = 150, message = "Name must be between 3 and 150 characters")
            String name,

            @Size(max = 1000, message = "Description must not exceed 1000 characters")
            String description,

            @NotNull(message = "Price is required")
            @DecimalMin(value = "0.00", message = "Price cannot be negative")
            @Digits(integer = 8, fraction = 2, message = "Invalid price format")
            BigDecimal price,

            @NotNull(message = "Launching date/time is required")
            @FutureOrPresent(message = "Launching date/time must be present or future")
            LocalDateTime launchingDateTime,

            @NotNull(message = "Event date is required")
            @Future(message = "Event date must be in the future")
            LocalDate eventDate,

            @Min(value = 1, message = "Total quantity must be at least 1")
            int totalQuantity
    ) implements EventDef.CreateRequest {
    }

    @Builder
    public record Response(
            UUID id,
            String name,
            EventStatus status,
            String description,
            BigDecimal price,
            LocalDateTime launchingDateTime,
            LocalDate eventDate
    ) implements EventDef.Response {
    }

    @Builder
    public record AvailabilityResponse(
            UUID eventId,
            int availableStock
    ) implements EventDef.AvailabilityResponse {
    }
}
