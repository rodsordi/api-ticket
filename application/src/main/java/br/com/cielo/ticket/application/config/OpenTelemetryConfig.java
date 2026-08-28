package br.com.cielo.ticket.application.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.exporter.otlp.http.logs.OtlpHttpLogRecordExporter;
import io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.logs.export.BatchLogRecordProcessor;
import io.opentelemetry.sdk.resources.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenTelemetryConfig {

    @Value("${OTEL_EXPORTER_OTLP_LOGS_ENDPOINT:http://loki:3100/otlp/v1/logs}")
    private String lokiLogsEndpoint;

    @Value("${spring.application.name:api-ticket}")
    private String applicationName;

    @Bean
    public OpenTelemetry openTelemetry() {
        OtlpHttpLogRecordExporter logExporter = OtlpHttpLogRecordExporter.builder()
                .setEndpoint(lokiLogsEndpoint)
                .build();

        Resource resource = Resource.getDefault()
                .toBuilder()
                .put(AttributeKey.stringKey("service.name"), applicationName)
                .put(AttributeKey.stringKey("job"), applicationName)
                .build();

        SdkLoggerProvider loggerProvider = SdkLoggerProvider.builder()
                .setResource(resource)
                .addLogRecordProcessor(BatchLogRecordProcessor.builder(logExporter).build())
                .build();

        OpenTelemetrySdk openTelemetrySdk = OpenTelemetrySdk.builder()
                .setLoggerProvider(loggerProvider)
                .buildAndRegisterGlobal();

        OpenTelemetryAppender.install(openTelemetrySdk);

        return openTelemetrySdk;
    }
}
