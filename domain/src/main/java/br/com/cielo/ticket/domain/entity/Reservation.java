package br.com.cielo.ticket.domain.entity;

import br.com.cielo.commons.entity.AuditableEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

import static br.com.cielo.ticket.domain.entity.Reservation.Status.REQUESTED;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@SuperBuilder
public class Reservation extends AuditableEntity {

    public enum Status { REQUESTED, AWAITING_PAYMENT, PAYED, EXPIRED }

    private UUID id;

    @Builder.Default
    private Status status = REQUESTED;

    private String invoicePdfUrl;

    private UUID clientId;
}
