package br.com.cielo.ticket.application.v1.dto;

import br.com.cielo.ticket.application.v1.def.ReservationDef;
import br.com.cielo.ticket.domain.entity.enums.ReservationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class ReservationDto {

    @Builder
    public record ReserveRequest(
            @NotNull(message = "Event ID is required")
            UUID eventId
    ) implements ReservationDef.ReserveRequest {
    }

    @Builder
    public record RequestResponse(
            UUID protocolId
    ) implements ReservationDef.RequestResponse {
    }

    @Builder
    public record Response(
            UUID id,
            ReservationStatus status,
            String invoicePdfUrl
    ) implements ReservationDef.Response {
    }
}
