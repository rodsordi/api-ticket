package br.com.cielo.commons.aspect;

import br.com.cielo.commons.annotation.Idempotent;
import br.com.cielo.commons.dto.IdempotentResponseDto;
import br.com.cielo.commons.exception.TooManyRequestsException;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Boolean.TRUE;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class IdempotencyAspect {

    private static final String IN_PROGRESS = "IN_PROGRESS";
    private static final String REDIS_PREFIX = "idempotency:reservation:";

    private final RedisTemplate<String, Object> redisTemplate;
    private final Gson gson;
    private final MeterRegistry meterRegistry;

    @Around("@annotation(idempotent)")
    public Object handleIdempotency(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        HttpServletRequest request = getHttpServletRequest();

        if (request == null) {
            return joinPoint.proceed();
        }

        String idempotencyKey = request.getHeader(idempotent.headerName());
        if (idempotencyKey == null || idempotencyKey.trim().isEmpty()) {
            return joinPoint.proceed();
        }

        String redisKey = REDIS_PREFIX + idempotencyKey.trim();

        Boolean isNewKey = redisTemplate.opsForValue().setIfAbsent(redisKey, IN_PROGRESS, Duration.ofSeconds(30));

        if (!TRUE.equals(isNewKey)) {
            Object cachedValue = redisTemplate.opsForValue().get(redisKey);

            if (IN_PROGRESS.equals(cachedValue)) {
                log.warn("Concurrent request detected for idempotency key: {}", idempotencyKey);
                throw new TooManyRequestsException("Request is currently in progress. Please try again shortly.");
            }

            if (cachedValue instanceof IdempotentResponseDto dto) {
                log.info("Returning cached idempotent response for key: {}", idempotencyKey);
                meterRegistry.counter("ticket.idempotency.hit.total").increment();
                return rebuildResponseEntity(dto);
            }

            log.warn("Unknown state in Redis for idempotency key: {}, proceeding with execution", idempotencyKey);
        }

        try {
            Object result = joinPoint.proceed();

            if (result instanceof ResponseEntity<?> responseEntity) {
                IdempotentResponseDto dto = buildResponseDto(responseEntity);
                redisTemplate.opsForValue().set(redisKey, dto, Duration.ofSeconds(idempotent.ttlSeconds()));
            }

            return result;
        } catch (Throwable t) {
            log.error("Execution failed for idempotency key: {}, removing Redis key", idempotencyKey);
            redisTemplate.delete(redisKey);
            throw t;
        }
    }

    private HttpServletRequest getHttpServletRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    private IdempotentResponseDto buildResponseDto(ResponseEntity<?> responseEntity) {
        Map<String, String> headersMap = new HashMap<>();
        HttpHeaders headers = responseEntity.getHeaders();
        headers.forEach((key, values) -> {
            if (values != null && !values.isEmpty()) {
                headersMap.put(key, values.get(0));
            }
        });

        String jsonBody = null;
        if (responseEntity.getBody() != null) {
            try {
                if (responseEntity.getBody() instanceof String str) {
                    jsonBody = str;
                } else {
                    jsonBody = gson.toJson(responseEntity.getBody());
                }
            } catch (Exception e) {
                log.error("Failed to serialize response body to JSON via Gson", e);
                jsonBody = responseEntity.getBody().toString();
            }
        }

        return IdempotentResponseDto.builder()
                .statusCode(responseEntity.getStatusCode().value())
                .headers(headersMap)
                .jsonBody(jsonBody)
                .build();
    }

    private ResponseEntity<Object> rebuildResponseEntity(IdempotentResponseDto dto) {
        HttpHeaders headers = new HttpHeaders();
        if (dto.getHeaders() != null) {
            dto.getHeaders().forEach(headers::add);
        }
        headers.setContentType(MediaType.APPLICATION_JSON);

        return ResponseEntity.status(dto.getStatusCode())
                .headers(headers)
                .body(dto.getJsonBody());
    }
}
