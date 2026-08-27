package br.com.cielo.ticket.domain.usecase;

import br.com.cielo.commons.exception.ResourceNotFoundException;
import br.com.cielo.ticket.domain.entity.Client;
import br.com.cielo.ticket.domain.entity.Reservation;
import br.com.cielo.ticket.domain.entity.enums.ReservationStatus;
import br.com.cielo.ticket.domain.repository.ReservationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetReservationUseCase Test Suite")
class GetReservationUseCaseTest {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private GetReservationUseCase getReservationUseCase;

    private Client createMockClient() {
        return Client.builder()
                .id(UUID.randomUUID())
                .fullName("John Doe")
                .email("john@example.com")
                .document("12345678901")
                .birthDate(LocalDate.now().minusYears(25))
                .build();
    }

    @Nested
    @DisplayName("execute() method")
    class Execute {

        @Nested
        @DisplayName("Success Scenarios")
        class Success {

            @Test
            @DisplayName("should return reservation when reservation exists")
            void shouldReturnReservationWhenExists() {
                // Arrange
                var reservationId = UUID.randomUUID();
                var reservation = Reservation.builder()
                        .id(reservationId)
                        .status(ReservationStatus.REQUESTED)
                        .client(createMockClient())
                        .build();

                when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

                // Act
                var result = getReservationUseCase.execute(reservationId);

                // Assert
                assertThat(result).isNotNull();
                assertThat(result.getId()).isEqualTo(reservationId);
            }
        }

        @Nested
        @DisplayName("Failure Scenarios")
        class Failure {

            @Test
            @DisplayName("should throw ResourceNotFoundException when reservation does not exist")
            void shouldThrowResourceNotFoundExceptionWhenDoesNotExist() {
                // Arrange
                var reservationId = UUID.randomUUID();
                when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());

                // Act & Assert
                assertThatThrownBy(() -> getReservationUseCase.execute(reservationId))
                        .isInstanceOf(ResourceNotFoundException.class);
            }
        }
    }
}
