package br.com.cielo.commons.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP;

@Slf4j
@Configuration
public class SwaggerConfig {

    @Value("${info.app.title:Ticket API}")
    private String appTitle;

    @Value("${info.app.version:1.0.0}")
    private String appVersion;

    @Value("${info.app.description:API de reserva de ingressos e gestão de eventos em alta concorrência}")
    private String appDescription;

    @Bean
    public OpenAPI openAPI(Info info, Collection<Map.Entry<String, Example>> examples) {
        final var securitySchemeName = "bearerAuth";

        var localServer = new Server()
                .url("http://localhost:8080/api")
                .description("Servidor Local / Desenvolvimento");

        var eventsTag = new Tag()
                .name("Events (v1)")
                .description("Endpoints para cadastro e consulta de eventos e estoque de ingressos");

        var reservationsTag = new Tag()
                .name("Reservations (v1)")
                .description("Endpoints para solicitação, consulta e cancelamento de reservas de ingressos");

        var openApi = new OpenAPI()
                .info(info)
                .servers(List.of(localServer))
                .tags(List.of(eventsTag, reservationsTag))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components().addSecuritySchemes(securitySchemeName, new SecurityScheme()
                        .name(securitySchemeName)
                        .type(HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("Autenticação via Token JWT Bearer enviado no cabeçalho Authorization")));

        if (examples != null) {
            examples.forEach(example -> openApi.getComponents()
                    .addExamples(example.getKey(), example.getValue()));
        }

        return openApi;
    }

    @Bean
    public Info info() {
        var contact = new Contact()
                .name("Cielo Engineering Team")
                .email("dev@cielo.com.br")
                .url("https://cielo.com.br");

        var license = new License()
                .name("Apache 2.0")
                .url("https://www.apache.org/licenses/LICENSE-2.0");

        return new Info()
                .title(appTitle)
                .version(appVersion)
                .description(appDescription)
                .contact(contact)
                .license(license);
    }
}
