package br.com.cielo.ticket.domain.usecase;

import br.com.cielo.commons.exception.BusinessException;
import br.com.cielo.commons.exception.ResourceNotFoundException;
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

import java.util.Optional;
import java.util.UUID;

import static br.com.cielo.ticket.domain.entity.factory.ClientFactory.create_Client;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CancelReservationUseCase Test Suite")
class CancelReservationUseCaseTest {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private CancelReservationUseCase cancelReservationUseCase;

    @Nested
    @DisplayName("execute() method")
    class Execute {

        @Nested
        @DisplayName("Success Scenarios")
        class Success {

            @Test
            @DisplayName("should cancel reservation successfully when initial status is valid")
            void shouldCancelReservationSuccessfullyWhenValid() {
                // Arrange
                var reservationId = UUID.randomUUID();
                var reservation = Reservation.builder()
                        .id(reservationId)
                        .status(ReservationStatus.REQUESTED)
                        .client(create_Client().valid())
                        .build();

                when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
                when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));

                // Act
                var result = cancelReservationUseCase.execute(reservationId);

                // Assert
                assertThat(result).isNotNull();
                assertThat(result.getStatus()).isEqualTo(ReservationStatus.CANCELED);
                verify(reservationRepository).save(any(Reservation.class));
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
                assertThatThrownBy(() -> cancelReservationUseCase.execute(reservationId))
                        .isInstanceOf(ResourceNotFoundException.class);
            }

            @Test
            @DisplayName("should throw BusinessException when reservation is already expired")
            void shouldThrowBusinessExceptionWhenAlreadyExpired() {
                // Arrange
                var reservationId = UUID.randomUUID();
                var reservation = Reservation.builder()
                        .id(reservationId)
                        .status(ReservationStatus.EXPIRED)
                        .client(create_Client().valid())
                        .build();

                when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

                // Act & Assert
                assertThatThrownBy(() -> cancelReservationUseCase.execute(reservationId))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining("Transição inválida");
            }
        }
    }
}
