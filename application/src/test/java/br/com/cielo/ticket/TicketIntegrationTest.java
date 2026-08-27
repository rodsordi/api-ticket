package br.com.cielo.ticket;

import br.com.cielo.commons.iandt.setup.CassandraSetup;
import br.com.cielo.commons.iandt.setup.KafkaSetup;
import br.com.cielo.commons.iandt.setup.KeycloakSetup;
import br.com.cielo.commons.iandt.setup.RedisSetup;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.env.Environment;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static java.lang.String.format;
import static org.springframework.core.env.Profiles.of;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-int_test.properties")
public abstract class TicketIntegrationTest implements CassandraSetup, RedisSetup, KafkaSetup, KeycloakSetup {

    @Autowired
    private Environment env;

    @LocalServerPort
    private Integer port;

    @Autowired(required = false)
    private List<CassandraRepository<?, ?>> repositories;

    @BeforeEach
    void beforeEach() {
        RestAssured.baseURI = format("http://localhost:%s/api", port);
        String token = KeycloakSetup.getAccessToken();
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .addHeader("Authorization", "Bearer " + token)
                .build();

        if (env.acceptsProfiles(of("int_test")) && repositories != null) {
            log.info("Deleting all test data");
            for (var i = repositories.size() - 1; i >= 0; i--) {
                var repository = repositories.get(i);
                repository.deleteAll();
            }
        }
    }
}
