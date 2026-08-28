package br.com.cielo.commons.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SwaggerConfigTest {

    @Test
    @DisplayName("Should build OpenAPI bean with application info, servers, security scheme, and tags")
    void shouldBuildOpenApiBeanWithInfoAndSecurityScheme() {
        var swaggerConfig = new SwaggerConfig();
        ReflectionTestUtils.setField(swaggerConfig, "appTitle", "Ticket API");
        ReflectionTestUtils.setField(swaggerConfig, "appVersion", "1.0.0");
        ReflectionTestUtils.setField(swaggerConfig, "appDescription", "High concurrency ticket reservation API");

        var info = swaggerConfig.info();
        assertThat(info).isNotNull();
        assertThat(info.getTitle()).isEqualTo("Ticket API");
        assertThat(info.getVersion()).isEqualTo("1.0.0");
        assertThat(info.getDescription()).contains("High concurrency ticket reservation API");
        assertThat(info.getContact()).isNotNull();
        assertThat(info.getContact().getName()).isEqualTo("Cielo Engineering Team");

        List<Map.Entry<String, Example>> examples = List.of();
        OpenAPI openAPI = swaggerConfig.openAPI(info, examples);

        assertThat(openAPI).isNotNull();
        assertThat(openAPI.getInfo()).isNotNull();
        assertThat(openAPI.getInfo().getTitle()).isEqualTo("Ticket API");
        assertThat(openAPI.getServers()).isNotEmpty();
        assertThat(openAPI.getServers().get(0).getUrl()).isEqualTo("http://localhost:8080/api");

        assertThat(openAPI.getSecurity()).isNotEmpty();
        assertThat(openAPI.getComponents()).isNotNull();
        assertThat(openAPI.getComponents().getSecuritySchemes()).containsKey("bearerAuth");

        var bearerAuthScheme = openAPI.getComponents().getSecuritySchemes().get("bearerAuth");
        assertThat(bearerAuthScheme).isNotNull();
        assertThat(bearerAuthScheme.getType()).isEqualTo(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP);
        assertThat(bearerAuthScheme.getScheme()).isEqualTo("bearer");
        assertThat(bearerAuthScheme.getBearerFormat()).isEqualTo("JWT");

        assertThat(openAPI.getTags()).isNotEmpty();
        assertThat(openAPI.getTags()).extracting("name").contains("Events (v1)", "Reservations (v1)");
    }
}
