package br.com.cielo.ticket.domain.entity;

import br.com.cielo.commons.entity.AuditableEntity;
import br.com.cielo.ticket.domain.entity.enums.EventStatus;
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

import static br.com.cielo.ticket.domain.entity.enums.EventStatus.WAITING_LAUNCHING_DATE;
import static lombok.AccessLevel.PROTECTED;

@Table("events")
@Getter
@NoArgsConstructor(access = PROTECTED)
@SuperBuilder
@EqualsAndHashCode(callSuper = false, exclude = "id")
public class Event extends AuditableEntity {

    @PrimaryKey
    private UUID id;

    @Column("name")
    private String name;

    @Indexed
    @Column("status")
    @Builder.Default
    private EventStatus status = WAITING_LAUNCHING_DATE;

    @Column("description")
    private String description;

    @Column("price")
    private BigDecimal price;

    @Column("launching_date_time")
    private LocalDateTime launchingDateTime;

    @Column("event_date")
    private LocalDate eventDate;

    public void openForSale() {
        this.status = this.status.openForSale();
    }

    public void markOutOfStock() {
        this.status = this.status.markOutOfStock();
    }

    public void reopen() {
        this.status = this.status.reopen();
    }

    public void close() {
        this.status = this.status.close();
    }
}
