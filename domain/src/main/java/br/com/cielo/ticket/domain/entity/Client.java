package br.com.cielo.ticket.domain.entity;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
public class Client {

    private UUID id;

    private String fullName;

    private String email;

    private String document;

    private LocalDate birthDate;
}
