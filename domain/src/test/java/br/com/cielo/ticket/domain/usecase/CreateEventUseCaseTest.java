package br.com.cielo.ticket.domain.usecase;

import br.com.cielo.ticket.domain.entity.Event;
import br.com.cielo.ticket.domain.entity.enums.EventStatus;
import br.com.cielo.ticket.domain.repository.EventAvailabilityCacheRepository;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateEventUseCase Test Suite")
class CreateEventUseCaseTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventAvailabilityCacheRepository availabilityCacheRepository;

    @InjectMocks
    private CreateEventUseCase createEventUseCase;

    @Nested
    @DisplayName("execute() method")
    class Execute {

        @Nested
        @DisplayName("Success Scenarios")
        class Success {

            @Test
            @DisplayName("should validate and persist event directly without intermediate builder")
            void shouldCreateAndPersistEventSuccessfullyWhenValid() {
                // Arrange
                var eventInput = Event.builder()
                        .id(UUID.randomUUID())
                        .name("Rock in Rio")
                        .status(EventStatus.WAITING_LAUNCHING_DATE)
                        .description("Festival de Musica")
                        .price(new BigDecimal("350.00"))
                        .launchingDateTime(LocalDateTime.now().plusDays(5))
                        .eventDate(LocalDate.now().plusMonths(2))
                        .build();

                when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

                // Act
                var result = createEventUseCase.execute(eventInput, 500);

                // Assert
                assertThat(result).isNotNull();
                assertThat(result.getId()).isEqualTo(eventInput.getId());
                assertThat(result.getName()).isEqualTo("Rock in Rio");
                verify(eventRepository).save(eventInput);
                verify(availabilityCacheRepository).initAvailability(eq(eventInput.getId()), eq(500));
            }
        }
    }
}
