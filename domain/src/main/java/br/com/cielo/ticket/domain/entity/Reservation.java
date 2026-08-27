package br.com.cielo.ticket.domain.entity;

import br.com.cielo.commons.entity.AuditableEntity;
import br.com.cielo.ticket.domain.entity.enums.ReservationStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

import static br.com.cielo.ticket.domain.entity.enums.ReservationStatus.REQUESTED;
import static lombok.AccessLevel.PROTECTED;

@Table("reservations")
@Getter
@NoArgsConstructor(access = PROTECTED)
@SuperBuilder
public class Reservation extends AuditableEntity {

    @PrimaryKey
    @NotNull(message = "ID não pode ser nulo")
    private UUID id;

    @Column("status")
    @Builder.Default
    @NotNull(message = "Status da reserva é obrigatório")
    private ReservationStatus status = REQUESTED;

    @Column("invoice_pdf_url")
    @Pattern(regexp = "^https?://.*", message = "URL do comprovante deve ser válida")
    private String invoicePdfUrl;

    @Column("client")
    @Valid
    @NotNull(message = "Dados do cliente são obrigatórios")
    private Client client;

    public void markAwaitingPayment() {
        this.status = this.status.markAwaitingPayment();
    }

    public void markAwaitingPayment(String invoicePdfUrl) {
        this.status = this.status.markAwaitingPayment();
        this.invoicePdfUrl = invoicePdfUrl;
    }

    public void pay() {
        this.status = this.status.pay();
    }

    public void cancel() {
        this.status = this.status.cancel();
    }

    public void expire() {
        this.status = this.status.expire();
    }
}
