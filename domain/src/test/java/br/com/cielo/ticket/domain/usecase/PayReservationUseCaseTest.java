package br.com.cielo.ticket.domain.usecase;

import br.com.cielo.commons.exception.ResourceNotFoundException;
import br.com.cielo.ticket.domain.entity.Client;
import br.com.cielo.ticket.domain.entity.Reservation;
import br.com.cielo.ticket.domain.entity.enums.ReservationStatus;
import br.com.cielo.ticket.domain.port.ReservationEventPublisherPort;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PayReservationUseCase Test Suite")
class PayReservationUseCaseTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReservationEventPublisherPort eventPublisherPort;

    @InjectMocks
    private PayReservationUseCase payReservationUseCase;

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
            @DisplayName("should update reservation to PAYED and publish finished event")
            void shouldProcessPayedReservationSuccessfully() {
                // Arrange
                var reservationId = UUID.randomUUID();
                var client = createMockClient();
                var reservation = Reservation.builder()
                        .id(reservationId)
                        .status(ReservationStatus.AWAITING_PAYMENT)
                        .client(client)
                        .build();

                when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
                when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));

                // Act
                var result = payReservationUseCase.execute(reservationId);

                // Assert
                assertThat(result.getStatus()).isEqualTo(ReservationStatus.PAYED);
                verify(eventPublisherPort).publishFinished(reservationId, client.getId());
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
                assertThatThrownBy(() -> payReservationUseCase.execute(reservationId))
                        .isInstanceOf(ResourceNotFoundException.class);
            }
        }
    }
}
