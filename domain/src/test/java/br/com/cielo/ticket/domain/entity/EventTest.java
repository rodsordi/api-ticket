package br.com.cielo.ticket.domain.entity;

import br.com.cielo.commons.exception.BusinessException;
import br.com.cielo.ticket.domain.entity.enums.EventStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Event Domain Entity Test Suite")
class EventTest {

    @Nested
    @DisplayName("openForSale() method")
    class OpenForSale {

        @Nested
        @DisplayName("Success Scenarios")
        class Success {

            @ParameterizedTest(name = "Given status {0}, should successfully transition to {1}")
            @CsvSource({
                    "WAITING_LAUNCHING_DATE, OPENED_FOR_SALE"
            })
            @DisplayName("should transition status to OPENED_FOR_SALE when initial status is WAITING_LAUNCHING_DATE")
            void shouldTransitionStatusToOpenedForSaleWhenValid(EventStatus initialStatus, EventStatus expectedStatus) {
                // Arrange
                var event = Event.builder()
                        .id(UUID.randomUUID())
                        .status(initialStatus)
                        .build();

                // Act
                event.openForSale();

                // Assert
                assertThat(event.getStatus()).isEqualTo(expectedStatus);
            }
        }

        @Nested
        @DisplayName("Validation Scenarios")
        class ValidationFailure {

            @ParameterizedTest(name = "Given status {0}, should throw BusinessException")
            @CsvSource({
                    "OPENED_FOR_SALE, Transição inválida: não é possível ABRIR PARA VENDA a partir do estado OPENED_FOR_SALE",
                    "OUT_OF_STOCK, Transição inválida: não é possível ABRIR PARA VENDA a partir do estado OUT_OF_STOCK",
                    "REOPENED, Transição inválida: não é possível ABRIR PARA VENDA a partir do estado REOPENED",
                    "CLOSED, Transição inválida: não é possível ABRIR PARA VENDA a partir do estado CLOSED"
            })
            @DisplayName("should throw BusinessException with exact message when initial status is invalid")
            void shouldThrowExceptionWhenInitialStatusIsInvalid(EventStatus initialStatus, String expectedMessage) {
                // Arrange
                var event = Event.builder()
                        .id(UUID.randomUUID())
                        .status(initialStatus)
                        .build();

                // Act & Assert
                assertThatThrownBy(event::openForSale)
                        .isInstanceOf(BusinessException.class)
                        .hasMessage(expectedMessage);
            }
        }
    }

    @Nested
    @DisplayName("markOutOfStock() method")
    class MarkOutOfStock {

        @Nested
        @DisplayName("Success Scenarios")
        class Success {

            @ParameterizedTest(name = "Given status {0}, should successfully transition to {1}")
            @CsvSource({
                    "OPENED_FOR_SALE, OUT_OF_STOCK",
                    "REOPENED, OUT_OF_STOCK"
            })
            @DisplayName("should transition status to OUT_OF_STOCK when initial status is valid")
            void shouldTransitionStatusToOutOfStockWhenValid(EventStatus initialStatus, EventStatus expectedStatus) {
                // Arrange
                var event = Event.builder()
                        .id(UUID.randomUUID())
                        .status(initialStatus)
                        .build();

                // Act
                event.markOutOfStock();

                // Assert
                assertThat(event.getStatus()).isEqualTo(expectedStatus);
            }
        }

        @Nested
        @DisplayName("Validation Scenarios")
        class ValidationFailure {

            @ParameterizedTest(name = "Given status {0}, should throw BusinessException")
            @CsvSource({
                    "WAITING_LAUNCHING_DATE, Transição inválida: não é possível marcar SEM ESTOQUE a partir do estado WAITING_LAUNCHING_DATE",
                    "OUT_OF_STOCK, Transição inválida: não é possível marcar SEM ESTOQUE a partir do estado OUT_OF_STOCK",
                    "CLOSED, Transição inválida: não é possível marcar SEM ESTOQUE a partir do estado CLOSED"
            })
            @DisplayName("should throw BusinessException with exact message when initial status is invalid")
            void shouldThrowExceptionWhenInitialStatusIsInvalid(EventStatus initialStatus, String expectedMessage) {
                // Arrange
                var event = Event.builder()
                        .id(UUID.randomUUID())
                        .status(initialStatus)
                        .build();

                // Act & Assert
                assertThatThrownBy(event::markOutOfStock)
                        .isInstanceOf(BusinessException.class)
                        .hasMessage(expectedMessage);
            }
        }
    }

    @Nested
    @DisplayName("reopen() method")
    class Reopen {

        @Nested
        @DisplayName("Success Scenarios")
        class Success {

            @ParameterizedTest(name = "Given status {0}, should successfully transition to {1}")
            @CsvSource({
                    "OUT_OF_STOCK, REOPENED"
            })
            @DisplayName("should transition status to REOPENED when initial status is OUT_OF_STOCK")
            void shouldTransitionStatusToReopenedWhenValid(EventStatus initialStatus, EventStatus expectedStatus) {
                // Arrange
                var event = Event.builder()
                        .id(UUID.randomUUID())
                        .status(initialStatus)
                        .build();

                // Act
                event.reopen();

                // Assert
                assertThat(event.getStatus()).isEqualTo(expectedStatus);
            }
        }

        @Nested
        @DisplayName("Validation Scenarios")
        class ValidationFailure {

            @ParameterizedTest(name = "Given status {0}, should throw BusinessException")
            @CsvSource({
                    "WAITING_LAUNCHING_DATE, Transição inválida: não é possível REABRIR a partir do estado WAITING_LAUNCHING_DATE",
                    "OPENED_FOR_SALE, Transição inválida: não é possível REABRIR a partir do estado OPENED_FOR_SALE",
                    "REOPENED, Transição inválida: não é possível REABRIR a partir do estado REOPENED",
                    "CLOSED, Transição inválida: não é possível REABRIR a partir do estado CLOSED"
            })
            @DisplayName("should throw BusinessException with exact message when initial status is invalid")
            void shouldThrowExceptionWhenInitialStatusIsInvalid(EventStatus initialStatus, String expectedMessage) {
                // Arrange
                var event = Event.builder()
                        .id(UUID.randomUUID())
                        .status(initialStatus)
                        .build();

                // Act & Assert
                assertThatThrownBy(event::reopen)
                        .isInstanceOf(BusinessException.class)
                        .hasMessage(expectedMessage);
            }
        }
    }

    @Nested
    @DisplayName("close() method")
    class Close {

        @Nested
        @DisplayName("Success Scenarios")
        class Success {

            @ParameterizedTest(name = "Given status {0}, should successfully transition to {1}")
            @CsvSource({
                    "OPENED_FOR_SALE, CLOSED",
                    "OUT_OF_STOCK, CLOSED",
                    "REOPENED, CLOSED"
            })
            @DisplayName("should transition status to CLOSED when initial status is valid")
            void shouldTransitionStatusToClosedWhenValid(EventStatus initialStatus, EventStatus expectedStatus) {
                // Arrange
                var event = Event.builder()
                        .id(UUID.randomUUID())
                        .status(initialStatus)
                        .build();

                // Act
                event.close();

                // Assert
                assertThat(event.getStatus()).isEqualTo(expectedStatus);
            }
        }

        @Nested
        @DisplayName("Validation Scenarios")
        class ValidationFailure {

            @ParameterizedTest(name = "Given status {0}, should throw BusinessException")
            @CsvSource({
                    "WAITING_LAUNCHING_DATE, Transição inválida: não é possível FECHAR a partir do estado WAITING_LAUNCHING_DATE",
                    "CLOSED, Transição inválida: não é possível FECHAR a partir do estado CLOSED"
            })
            @DisplayName("should throw BusinessException with exact message when initial status is invalid")
            void shouldThrowExceptionWhenInitialStatusIsInvalid(EventStatus initialStatus, String expectedMessage) {
                // Arrange
                var event = Event.builder()
                        .id(UUID.randomUUID())
                        .status(initialStatus)
                        .build();

                // Act & Assert
                assertThatThrownBy(event::close)
                        .isInstanceOf(BusinessException.class)
                        .hasMessage(expectedMessage);
            }
        }
    }
}
