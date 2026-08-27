package br.com.cielo.ticket.application.v1.mapper;

import br.com.cielo.ticket.application.v1.dto.ReservationDto;
import br.com.cielo.ticket.domain.entity.Client;
import br.com.cielo.ticket.domain.entity.Reservation;
import org.mapstruct.Mapper;
import org.springframework.security.oauth2.jwt.Jwt;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    default Client toDomain(Jwt jwt) {
        if (jwt == null) {
            return null;
        }
        UUID id;
        try {
            id = UUID.fromString(jwt.getSubject());
        } catch (Exception e) {
            id = UUID.nameUUIDFromBytes(jwt.getSubject().getBytes(StandardCharsets.UTF_8));
        }

        String name = jwt.getClaimAsString("name");
        if (name == null || name.isBlank()) {
            name = jwt.getClaimAsString("preferred_username");
        }
        if (name == null || name.isBlank()) {
            name = "Test User";
        }

        String email = jwt.getClaimAsString("email");
        if (email == null || email.isBlank()) {
            email = "user@ticket.com";
        }

        String document = jwt.getClaimAsString("document");
        if (document == null || document.isBlank()) {
            document = "12345678901";
        }

        String birthDateStr = jwt.getClaimAsString("birth_date");
        LocalDate birthDate = birthDateStr != null ? LocalDate.parse(birthDateStr) : LocalDate.of(1995, 1, 1);

        return Client.builder()
                .id(id)
                .fullName(name)
                .email(email)
                .document(document)
                .birthDate(birthDate)
                .build();
    }

    ReservationDto.Response toResponse(Reservation reservation);
}
