package br.com.cielo.ticket.application.v1.def;

import br.com.cielo.ticket.domain.entity.enums.ReservationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public interface ReservationDef {

    interface ReserveRequest {

        @Schema(example = "1029deef-ed87-46c3-b345-a13b30168659", description = "ID do evento.")
        @NotNull(message = "Event ID is required")
        UUID eventId();
    }

    interface RequestResponse {

        @Schema(example = "767e1d48-9986-4559-8eed-4db955d9e757", description = "Protocolo da reserva solicitada.")
        UUID protocolId();
    }

    interface Response {

        @Schema(example = "767e1d48-9986-4559-8eed-4db955d9e757", description = "ID da reserva.")
        UUID id();

        @Schema(example = "AWAITING_PAYMENT", description = "Status da reserva.")
        ReservationStatus status();

        @Schema(example = "https://s3.amazonaws.com/invoices/inv-123.pdf", description = "URL da nota/fatura PDF.")
        String invoicePdfUrl();
    }
}
