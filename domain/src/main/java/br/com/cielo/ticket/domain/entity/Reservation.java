package br.com.cielo.ticket.domain.entity;

import br.com.cielo.commons.entity.AuditableEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.Indexed;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

import static br.com.cielo.ticket.domain.entity.Reservation.Status.REQUESTED;
import static lombok.AccessLevel.PROTECTED;

@Table("reservations")
@Getter
@NoArgsConstructor(access = PROTECTED)
@SuperBuilder
public class Reservation extends AuditableEntity {

    public enum Status { REQUESTED, AWAITING_PAYMENT, PAYED, EXPIRED }

    @PrimaryKey
    private UUID id;

    @Column("status")
    @Builder.Default
    private Status status = REQUESTED;

    @Column("invoice_pdf_url")
    private String invoicePdfUrl;

    @Indexed
    @Column("client_id")
    private UUID clientId;
}
