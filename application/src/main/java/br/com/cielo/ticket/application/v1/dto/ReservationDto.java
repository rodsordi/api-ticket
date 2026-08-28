package br.com.cielo.ticket.application.v1.dto;

import br.com.cielo.ticket.application.v1.def.ReservationDef;
import br.com.cielo.ticket.domain.entity.enums.ReservationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@Schema(description = "DTOs para operações e acompanhamento de reservas de ingressos")
public final class ReservationDto {

    @Builder
    @Schema(description = "Payload de requisição para solicitação de reserva de ingresso")
    public record ReserveRequest(
            @Schema(
                    description = "ID do evento para o qual se deseja reservar o ingresso (UUID v4)",
                    example = "1029deef-ed87-46c3-b345-a13b30168659",
                    requiredMode = Schema.RequiredMode.REQUIRED
            )
            @NotNull(message = "Event ID is required")
            UUID eventId
    ) implements ReservationDef.ReserveRequest {
    }

    @Builder
    @Schema(description = "Resposta do recebimento da solicitação de reserva assíncrona")
    public record RequestResponse(
            @Schema(
                    description = "ID do protocolo de acompanhamento da reserva gerado pelo sistema",
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            UUID protocolId
    ) implements ReservationDef.RequestResponse {
    }

    @Builder
    @Schema(description = "Representação dos detalhes da reserva de ingresso")
    public record Response(
            @Schema(description = "ID único da reserva (UUID v4)", example = "767e1d48-9986-4559-8eed-4db955d9e757")
            UUID id,

            @Schema(description = "Status atual do ciclo de vida da reserva", example = "CONFIRMED")
            ReservationStatus status,

            @Schema(
                    description = "URL pública de download do comprovante em PDF armazenado no S3",
                    example = "https://s3.amazonaws.com/ticket-invoices/invoices/767e1d48-9986-4559-8eed-4db955d9e757.pdf"
            )
            String invoicePdfUrl
    ) implements ReservationDef.Response {
    }
}
