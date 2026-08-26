package br.com.cielo.ticket.domain.entity;

import br.com.cielo.commons.exception.BusinessException;
import br.com.cielo.ticket.domain.entity.enums.ReservationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Reservation Domain Entity Test Suite")
class ReservationTest {

    @Nested
    @DisplayName("markAwaitingPayment() method")
    class MarkAwaitingPayment {

        @Nested
        @DisplayName("Success Scenarios")
        class Success {

            @ParameterizedTest(name = "Given status {0}, should successfully transition to {1}")
            @CsvSource({
                    "REQUESTED, AWAITING_PAYMENT"
            })
            @DisplayName("should transition status to AWAITING_PAYMENT when initial status is valid")
            void shouldTransitionStatusToAwaitingPaymentWhenValid(ReservationStatus initialStatus, ReservationStatus expectedStatus) {
                // Arrange
                var reservation = Reservation.builder()
                        .id(UUID.randomUUID())
                        .status(initialStatus)
                        .build();

                // Act
                reservation.markAwaitingPayment();

                // Assert
                assertThat(reservation.getStatus()).isEqualTo(expectedStatus);
            }
        }

        @Nested
        @DisplayName("Validation Scenarios")
        class ValidationFailure {

            @ParameterizedTest(name = "Given status {0}, should throw BusinessException")
            @CsvSource({
                    "AWAITING_PAYMENT, Transição inválida: não é possível alterar para AWAITING_PAYMENT a partir do estado AWAITING_PAYMENT",
                    "PAYED, Transição inválida: não é possível alterar para AWAITING_PAYMENT a partir do estado PAYED",
                    "EXPIRED, Transição inválida: não é possível alterar para AWAITING_PAYMENT a partir do estado EXPIRED"
            })
            @DisplayName("should throw BusinessException with exact message when initial status is invalid")
            void shouldThrowExceptionWhenInitialStatusIsInvalid(ReservationStatus initialStatus, String expectedMessage) {
                // Arrange
                var reservation = Reservation.builder()
                        .id(UUID.randomUUID())
                        .status(initialStatus)
                        .build();

                // Act & Assert
                assertThatThrownBy(reservation::markAwaitingPayment)
                        .isInstanceOf(BusinessException.class)
                        .hasMessage(expectedMessage);
            }
        }
    }

    @Nested
    @DisplayName("pay() method")
    class Pay {

        @Nested
        @DisplayName("Success Scenarios")
        class Success {

            @ParameterizedTest(name = "Given status {0}, should successfully transition to {1}")
            @CsvSource({
                    "AWAITING_PAYMENT, PAYED"
            })
            @DisplayName("should transition status to PAYED when initial status is AWAITING_PAYMENT")
            void shouldTransitionStatusToPayedWhenValid(ReservationStatus initialStatus, ReservationStatus expectedStatus) {
                // Arrange
                var reservation = Reservation.builder()
                        .id(UUID.randomUUID())
                        .status(initialStatus)
                        .build();

                // Act
                reservation.pay();

                // Assert
                assertThat(reservation.getStatus()).isEqualTo(expectedStatus);
            }
        }

        @Nested
        @DisplayName("Validation Scenarios")
        class ValidationFailure {

            @ParameterizedTest(name = "Given status {0}, should throw BusinessException")
            @CsvSource({
                    "REQUESTED, Transição inválida: não é possível alterar para PAYED a partir do estado REQUESTED",
                    "PAYED, Transição inválida: não é possível alterar para PAYED a partir do estado PAYED",
                    "EXPIRED, Transição inválida: não é possível alterar para PAYED a partir do estado EXPIRED"
            })
            @DisplayName("should throw BusinessException with exact message when initial status is invalid")
            void shouldThrowExceptionWhenInitialStatusIsInvalid(ReservationStatus initialStatus, String expectedMessage) {
                // Arrange
                var reservation = Reservation.builder()
                        .id(UUID.randomUUID())
                        .status(initialStatus)
                        .build();

                // Act & Assert
                assertThatThrownBy(reservation::pay)
                        .isInstanceOf(BusinessException.class)
                        .hasMessage(expectedMessage);
            }
        }
    }

    @Nested
    @DisplayName("expire() method")
    class Expire {

        @Nested
        @DisplayName("Success Scenarios")
        class Success {

            @ParameterizedTest(name = "Given status {0}, should successfully transition to {1}")
            @CsvSource({
                    "REQUESTED, EXPIRED",
                    "AWAITING_PAYMENT, EXPIRED"
            })
            @DisplayName("should transition status to EXPIRED when initial status is valid")
            void shouldTransitionStatusToExpiredWhenValid(ReservationStatus initialStatus, ReservationStatus expectedStatus) {
                // Arrange
                var reservation = Reservation.builder()
                        .id(UUID.randomUUID())
                        .status(initialStatus)
                        .build();

                // Act
                reservation.expire();

                // Assert
                assertThat(reservation.getStatus()).isEqualTo(expectedStatus);
            }
        }

        @Nested
        @DisplayName("Validation Scenarios")
        class ValidationFailure {

            @ParameterizedTest(name = "Given status {0}, should throw BusinessException")
            @CsvSource({
                    "PAYED, Transição inválida: não é possível alterar para EXPIRED a partir do estado PAYED",
                    "EXPIRED, Transição inválida: não é possível alterar para EXPIRED a partir do estado EXPIRED"
            })
            @DisplayName("should throw BusinessException with exact message when initial status is invalid")
            void shouldThrowExceptionWhenInitialStatusIsInvalid(ReservationStatus initialStatus, String expectedMessage) {
                // Arrange
                var reservation = Reservation.builder()
                        .id(UUID.randomUUID())
                .status(initialStatus)
                .build();

                // Act & Assert
                assertThatThrownBy(reservation::expire)
                        .isInstanceOf(BusinessException.class)
                        .hasMessage(expectedMessage);
            }
        }
    }
}
