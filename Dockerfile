# Usando a versão Alpine (que já é extremamente enxuta e segura por padrão)
FROM amazoncorretto:25-alpine

RUN addgroup -g 1000 appgroup && \
    adduser -u 1000 -G appgroup -D -s /bin/false appuser

WORKDIR /app

COPY --chown=appuser:appgroup application/target/api-ticket.application-0.0.1-SNAPSHOT.jar api-ticket.jar

USER appuser

ENTRYPOINT ["java","-jar","api-ticket.jar"]