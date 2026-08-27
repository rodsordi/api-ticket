package br.com.cielo.ticket.domain.usecase;

import br.com.cielo.ticket.domain.repository.EventAvailabilityCachePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CheckEventAvailabilityUseCase Test Suite")
class CheckEventAvailabilityUseCaseTest {

    @Mock
    private EventAvailabilityCachePort availabilityCachePort;

    @InjectMocks
    private CheckEventAvailabilityUseCase checkEventAvailabilityUseCase;

    @Nested
    @DisplayName("execute() method")
    class Execute {

        @Nested
        @DisplayName("Success Scenarios")
        class Success {

            @Test
            @DisplayName("should return stock count for given eventId")
            void shouldReturnStockForGivenEventId() {
                // Arrange
                var eventId = UUID.randomUUID();
                when(availabilityCachePort.getStock(eventId)).thenReturn(100);

                // Act
                var result = checkEventAvailabilityUseCase.execute(eventId);

                // Assert
                assertThat(result).isEqualTo(100);
            }
        }
    }
}
