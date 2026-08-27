package br.com.cielo.ticket.domain.usecase;

import br.com.cielo.commons.exception.BusinessException;
import br.com.cielo.ticket.domain.entity.Client;
import br.com.cielo.ticket.domain.entity.Event;
import br.com.cielo.ticket.domain.entity.Reservation;
import br.com.cielo.ticket.domain.entity.enums.ReservationStatus;
import br.com.cielo.ticket.domain.repository.EventAvailabilityCacheRepository;
import br.com.cielo.ticket.domain.repository.EventRepository;
import br.com.cielo.ticket.domain.repository.PaymentGatewayRepository;
import br.com.cielo.ticket.domain.repository.ReservationEventPublisherRepository;
import br.com.cielo.ticket.domain.repository.ReservationRepository;
import br.com.cielo.ticket.domain.repository.S3StorageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReserveTicketUseCase Test Suite")
class ReserveTicketUseCaseTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private PaymentGatewayRepository paymentGatewayRepository;

    @Mock
    private S3StorageRepository s3StorageRepository;

    @Mock
    private ReservationEventPublisherRepository eventPublisherRepository;

    @Mock
    private EventAvailabilityCacheRepository availabilityCacheRepository;

    private ReserveTicketUseCase reserveTicketUseCase;

    @BeforeEach
    void setUp() {
        reserveTicketUseCase = new ReserveTicketUseCase(
                reservationRepository,
                eventRepository,
                paymentGatewayRepository,
                s3StorageRepository,
                eventPublisherRepository,
                availabilityCacheRepository,
                15L
        );
    }

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
    @DisplayName("request() method")
    class Request {

        @Nested
        @DisplayName("Success Scenarios")
        class Success {

            @Test
            @DisplayName("should validate client, decrement Redis stock, publish requested event and return protocol")
            void shouldValidateClientDecrementStockAndPublishEventWhenAvailable() {
                // Arrange
                var eventId = UUID.randomUUID();
                var client = createMockClient();

                when(availabilityCacheRepository.tryDecrement(eventId)).thenReturn(true);

                // Act
                var protocolId = reserveTicketUseCase.request(eventId, client);

                // Assert
                assertThat(protocolId).isNotNull();
                verify(availabilityCacheRepository).tryDecrement(eventId);
                verify(eventPublisherRepository).publishRequested(any(UUID.class), eq(eventId), eq(client.getId()));
            }
        }

        @Nested
        @DisplayName("Failure Scenarios")
        class Failure {

            @Test
            @DisplayName("should throw BusinessException when Redis stock decrement fails")
            void shouldThrowBusinessExceptionWhenOutOfStock() {
                // Arrange
                var eventId = UUID.randomUUID();
                var client = createMockClient();

                when(availabilityCacheRepository.tryDecrement(eventId)).thenReturn(false);

                // Act & Assert
                assertThatThrownBy(() -> reserveTicketUseCase.request(eventId, client))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining("Ingressos esgotados ou indisponíveis");
            }
        }
    }

    @Nested
    @DisplayName("processRequested() method")
    class ProcessRequested {

        @Nested
        @DisplayName("Success Scenarios")
        class Success {

            @Test
            @DisplayName("should validate client, generate invoice PDF, upload to S3 and publish created event with configured delay")
            void shouldProcessRequestedReservationSuccessfully() {
                // Arrange
                var reservationId = UUID.randomUUID();
                var eventId = UUID.randomUUID();
                var client = createMockClient();

                var event = Event.builder().id(eventId).price(new BigDecimal("100.00")).build();
                var pdfBytes = "PDF_CONTENT".getBytes();
                var s3Url = "https://s3.amazonaws.com/invoices/inv-123.pdf";

                when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
                when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));
                when(paymentGatewayRepository.generateInvoicePdf(eq(reservationId), eq(client.getId()), any())).thenReturn(pdfBytes);
                when(s3StorageRepository.uploadInvoicePdf(reservationId, pdfBytes)).thenReturn(s3Url);

                // Act
                var result = reserveTicketUseCase.processRequested(reservationId, eventId, client);

                // Assert
                assertThat(result).isNotNull();
                assertThat(result.getStatus()).isEqualTo(ReservationStatus.AWAITING_PAYMENT);
                assertThat(result.getInvoicePdfUrl()).isEqualTo(s3Url);

                verify(eventPublisherRepository).publishCreated(reservationId, client.getId(), s3Url);
                verify(eventPublisherRepository).publishExpiredDelay(reservationId, eventId, 15L);
            }
        }
    }
}
