package br.com.cielo.ticket.domain.entity;

import lombok.Builder;
import lombok.Getter;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.Indexed;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDate;
import java.util.UUID;

@Table("clients")
@Getter
@Builder
public class Client {

    @PrimaryKey
    private UUID id;

    @Column("full_name")
    private String fullName;

    @Indexed
    @Column("email")
    private String email;

    @Indexed
    @Column("document")
    private String document;

    @Column("birth_date")
    private LocalDate birthDate;
}
