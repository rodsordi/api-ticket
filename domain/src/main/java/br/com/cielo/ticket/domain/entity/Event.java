package br.com.cielo.ticket.domain.entity;

import br.com.cielo.commons.entity.AuditableEntity;
import br.com.cielo.ticket.domain.entity.enums.EventStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotNull(message = "ID não pode ser nulo")
    private UUID id;

    @Column("name")
    @NotBlank(message = "Nome do evento é obrigatório")
    @Size(min = 3, max = 150, message = "Nome do evento deve conter entre 3 e 150 caracteres")
    private String name;

    @Indexed
    @Column("status")
    @Builder.Default
    @NotNull(message = "Status do evento é obrigatório")
    private EventStatus status = WAITING_LAUNCHING_DATE;

    @Column("description")
    @Size(max = 1000, message = "Descrição deve ter no máximo 1000 caracteres")
    private String description;

    @Column("price")
    @NotNull(message = "Preço é obrigatório")
    @DecimalMin(value = "0.00", message = "Preço não pode ser negativo")
    @Digits(integer = 8, fraction = 2, message = "Preço inválido")
    private BigDecimal price;

    @Column("launching_date_time")
    @NotNull(message = "Data/hora de lançamento é obrigatória")
    @FutureOrPresent(message = "Data/hora de lançamento deve ser no presente ou futuro")
    private LocalDateTime launchingDateTime;

    @Column("event_date")
    @NotNull(message = "Data do evento é obrigatória")
    @Future(message = "Data do evento deve ser no futuro")
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
