package br.com.cielo.ticket.domain.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import org.springframework.data.cassandra.core.mapping.UserDefinedType;

import java.time.LocalDate;
import java.util.UUID;

import static lombok.AccessLevel.PROTECTED;

@UserDefinedType("client_type")
@Getter
@NoArgsConstructor(access = PROTECTED)
@SuperBuilder
public class Client {

    @NotNull(message = "ID do cliente é obrigatório")
    private UUID id;

    @NotBlank(message = "Nome do cliente é obrigatório")
    private String fullName;

    @NotBlank(message = "CPF do cliente é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter exatamente 11 dígitos numéricos")
    private String document;

    @NotBlank(message = "E-mail do cliente é obrigatório")
    @Email(message = "E-mail do cliente deve ser válido")
    private String email;

    @NotNull(message = "Data de nascimento é obrigatória")
    @Past(message = "Data de nascimento deve ser no passado")
    private LocalDate birthDate;
}
