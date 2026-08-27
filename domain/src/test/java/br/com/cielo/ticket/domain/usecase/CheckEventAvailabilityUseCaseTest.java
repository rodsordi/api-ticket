package br.com.cielo.ticket.domain.usecase;

import br.com.cielo.ticket.domain.repository.EventAvailabilityCacheRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
    private EventAvailabilityCacheRepository availabilityCacheRepository;

    @InjectMocks
    private CheckEventAvailabilityUseCase checkEventAvailabilityUseCase;

    @Nested
    @DisplayName("execute() method")
    class Execute {

        @Nested
        @DisplayName("Success Scenarios")
        class Success {

            @ParameterizedTest(name = "Given Redis availability is {0}, should return {0}")
            @ValueSource(booleans = {true, false})
            @DisplayName("should return boolean availability result from Redis cache")
            void shouldReturnAvailabilityResultFromRedisCache(boolean expectedAvailability) {
                // Arrange
                var eventId = UUID.randomUUID();
                when(availabilityCacheRepository.isAvailable(eventId)).thenReturn(expectedAvailability);

                // Act
                var result = checkEventAvailabilityUseCase.execute(eventId);

                // Assert
                assertThat(result).isEqualTo(expectedAvailability);
            }
        }
    }
}
