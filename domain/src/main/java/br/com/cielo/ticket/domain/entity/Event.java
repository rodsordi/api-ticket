package br.com.cielo.ticket.domain.entity;

import br.com.cielo.commons.entity.AuditableEntity;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static br.com.cielo.ticket.domain.entity.Event.Status.WAITING_LAUNCHING_DATE;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@SuperBuilder
@EqualsAndHashCode(callSuper = false, exclude = "id")
public class Event extends AuditableEntity {

    public enum Status {
        WAITING_LAUNCHING_DATE,
        OPENED_FOR_SALE,
        OUT_OF_STOCK,
        REOPENED,
        CLOSED
    }

    private UUID id;

    private String name;

    @Builder.Default
    private Status status = WAITING_LAUNCHING_DATE;

    private String description;

    private BigDecimal price;

    private LocalDateTime launchingDateTime;

    private LocalDate eventDate;
}
