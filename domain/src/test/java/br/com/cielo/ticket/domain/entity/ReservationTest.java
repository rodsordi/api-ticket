package br.com.cielo.ticket.domain.entity;

import br.com.cielo.commons.exception.BusinessException;
import br.com.cielo.ticket.domain.entity.enums.ReservationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static br.com.cielo.ticket.domain.entity.factory.ClientFactory.create_Client;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Reservation Unit Test Suite")
class ReservationTest {

    @Nested
    @DisplayName("markAwaitingPayment() method")
    class MarkAwaitingPayment {

        @Nested
        @DisplayName("Success Scenarios")
        class Success {

            @Test
            @DisplayName("should transition from REQUESTED to AWAITING_PAYMENT")
            void shouldTransitionToAwaitingPayment() {
                var reservation = Reservation.builder()
                        .id(UUID.randomUUID())
                        .status(ReservationStatus.REQUESTED)
                        .client(create_Client().valid())
                        .build();

                reservation.markAwaitingPayment("https://s3.amazonaws.com/invoices/inv-1.pdf");

                assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.AWAITING_PAYMENT);
                assertThat(reservation.getInvoicePdfUrl()).isEqualTo("https://s3.amazonaws.com/invoices/inv-1.pdf");
            }
        }

        @Nested
        @DisplayName("Failure Scenarios")
        class Failure {

            @Test
            @DisplayName("should throw BusinessException when transitioning from EXPIRED")
            void shouldThrowExceptionWhenExpired() {
                var reservation = Reservation.builder()
                        .id(UUID.randomUUID())
                        .status(ReservationStatus.EXPIRED)
                        .client(create_Client().valid())
                        .build();

                assertThatThrownBy(reservation::markAwaitingPayment)
                        .isInstanceOf(BusinessException.class);
            }
        }
    }

    @Nested
    @DisplayName("pay() method")
    class Pay {

        @Nested
        @DisplayName("Success Scenarios")
        class Success {

            @Test
            @DisplayName("should transition from AWAITING_PAYMENT to PAYED")
            void shouldTransitionToPayed() {
                var reservation = Reservation.builder()
                        .id(UUID.randomUUID())
                        .status(ReservationStatus.AWAITING_PAYMENT)
                        .client(create_Client().valid())
                        .build();

                reservation.pay();

                assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.PAYED);
            }
        }

        @Nested
        @DisplayName("Failure Scenarios")
        class Failure {

            @Test
            @DisplayName("should throw BusinessException when transitioning from EXPIRED to PAYED")
            void shouldThrowExceptionWhenExpired() {
                var reservation = Reservation.builder()
                        .id(UUID.randomUUID())
                        .status(ReservationStatus.EXPIRED)
                        .client(create_Client().valid())
                        .build();

                assertThatThrownBy(reservation::pay)
                        .isInstanceOf(BusinessException.class);
            }
        }
    }

    @Nested
    @DisplayName("cancel() method")
    class Cancel {

        @Nested
        @DisplayName("Success Scenarios")
        class Success {

            @Test
            @DisplayName("should transition from REQUESTED to CANCELED")
            void shouldTransitionToCanceled() {
                var reservation = Reservation.builder()
                        .id(UUID.randomUUID())
                        .status(ReservationStatus.REQUESTED)
                        .client(create_Client().valid())
                        .build();

                reservation.cancel();

                assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELED);
            }
        }

        @Nested
        @DisplayName("Failure Scenarios")
        class Failure {

            @Test
            @DisplayName("should throw BusinessException when transitioning from EXPIRED to CANCELED")
            void shouldThrowExceptionWhenExpired() {
                var reservation = Reservation.builder()
                        .id(UUID.randomUUID())
                        .status(ReservationStatus.EXPIRED)
                        .client(create_Client().valid())
                        .build();

                assertThatThrownBy(reservation::cancel)
                        .isInstanceOf(BusinessException.class);
            }
        }
    }

    @Nested
    @DisplayName("expire() method")
    class Expire {

        @Nested
        @DisplayName("Success Scenarios")
        class Success {

            @Test
            @DisplayName("should transition from AWAITING_PAYMENT to EXPIRED")
            void shouldTransitionToExpired() {
                var reservation = Reservation.builder()
                        .id(UUID.randomUUID())
                        .status(ReservationStatus.AWAITING_PAYMENT)
                        .client(create_Client().valid())
                        .build();

                reservation.expire();

                assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.EXPIRED);
            }
        }

        @Nested
        @DisplayName("Failure Scenarios")
        class Failure {

            @Test
            @DisplayName("should throw BusinessException when transitioning from PAYED to EXPIRED")
            void shouldThrowExceptionWhenPayed() {
                var reservation = Reservation.builder()
                        .id(UUID.randomUUID())
                        .status(ReservationStatus.PAYED)
                        .client(create_Client().valid())
                        .build();

                assertThatThrownBy(reservation::expire)
                        .isInstanceOf(BusinessException.class);
            }
        }
    }
}
