package br.com.cielo.ticket.application.adapter;

import br.com.cielo.ticket.domain.port.EventAvailabilityCachePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventAvailabilityCacheAdapter implements EventAvailabilityCachePort {

    private static final String STOCK_KEY_PREFIX = "event:stock:";
    private final StringRedisTemplate redisTemplate;

    @Override
    public void initializeStock(UUID eventId, int totalQuantity) {
        var key = STOCK_KEY_PREFIX + eventId;
        log.info("Initializing Redis stock for event {} with quantity {}", eventId, totalQuantity);
        redisTemplate.opsForValue().set(key, String.valueOf(totalQuantity));
    }

    @Override
    public boolean tryDecrement(UUID eventId) {
        var key = STOCK_KEY_PREFIX + eventId;
        var currentStock = getStock(eventId);
        if (currentStock <= 0) {
            log.warn("Attempted to decrement out of stock event {}", eventId);
            return false;
        }
        var remaining = redisTemplate.opsForValue().decrement(key);
        log.info("Decremented stock for event {}. Remaining: {}", eventId, remaining);
        return remaining != null && remaining >= 0;
    }

    @Override
    public void increment(UUID eventId) {
        var key = STOCK_KEY_PREFIX + eventId;
        log.info("Incrementing stock for event {}", eventId);
        redisTemplate.opsForValue().increment(key);
    }

    @Override
    public int getStock(UUID eventId) {
        var key = STOCK_KEY_PREFIX + eventId;
        var val = redisTemplate.opsForValue().get(key);
        if (val == null) {
            return 0;
        }
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
