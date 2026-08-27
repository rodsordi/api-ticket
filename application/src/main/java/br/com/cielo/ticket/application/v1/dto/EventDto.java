package br.com.cielo.ticket.application.v1.dto;

import br.com.cielo.ticket.application.v1.def.EventDef;
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

            @NotNull(message = "Available quantity is required")
            @Min(value = 1, message = "Available quantity must be at least 1")
            Integer availableQuantity,

            @NotNull(message = "Event date is required")
            @Future(message = "Event date must be in the future")
            LocalDate eventDate
    ) implements EventDef.CreateRequest {
    }

    @Builder
    public record Response(
            UUID id,
            String name,
            String description,
            BigDecimal price,
            Integer availableQuantity,
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
