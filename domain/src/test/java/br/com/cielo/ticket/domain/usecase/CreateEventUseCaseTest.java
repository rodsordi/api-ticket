package br.com.cielo.ticket.domain.usecase;

import br.com.cielo.ticket.domain.entity.Event;
import br.com.cielo.ticket.domain.port.EventAvailabilityCachePort;
import br.com.cielo.ticket.domain.repository.EventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateEventUseCase Test Suite")
class CreateEventUseCaseTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventAvailabilityCachePort availabilityCachePort;

    @InjectMocks
    private CreateEventUseCase createEventUseCase;

    @Nested
    @DisplayName("execute() method")
    class Execute {

        @Nested
        @DisplayName("Success Scenarios")
        class Success {

            @Test
            @DisplayName("should save event and initialize Redis stock successfully")
            void shouldSaveEventAndInitializeStockSuccessfully() {
                // Arrange
                var eventId = UUID.randomUUID();
                var event = Event.builder()
                        .id(eventId)
                        .name("Rock in Rio")
                        .description("Festival de Música")
                        .launchingDateTime(LocalDateTime.now().plusDays(1))
                        .eventDate(LocalDate.now().plusMonths(2))
                        .price(new BigDecimal("350.00"))
                        .build();

                when(eventRepository.save(any(Event.class))).thenReturn(event);

                // Act
                var result = createEventUseCase.execute(event, 50000);

                // Assert
                assertThat(result).isNotNull();
                assertThat(result.getId()).isEqualTo(eventId);
                verify(eventRepository).save(event);
                verify(availabilityCachePort).initializeStock(eventId, 50000);
            }
        }
    }
}
