package br.com.cielo.ticket.domain.entity;

import br.com.cielo.commons.entity.AuditableEntity;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.Indexed;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static br.com.cielo.ticket.domain.entity.Event.Status.WAITING_LAUNCHING_DATE;
import static lombok.AccessLevel.PROTECTED;

@Table("events")
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

    @PrimaryKey
    private UUID id;

    @Column("name")
    private String name;

    @Indexed
    @Column("status")
    @Builder.Default
    private Status status = WAITING_LAUNCHING_DATE;

    @Column("description")
    private String description;

    @Column("price")
    private BigDecimal price;

    @Column("launching_date_time")
    private LocalDateTime launchingDateTime;

    @Column("event_date")
    private LocalDate eventDate;
}
