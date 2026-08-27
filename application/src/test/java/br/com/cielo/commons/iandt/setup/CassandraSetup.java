package br.com.cielo.commons.iandt.setup;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.CassandraContainer;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public interface CassandraSetup {

    CassandraContainer<?> CASSANDRA = new CassandraContainer<>("cassandra:5.0")
            .withAccessToHost(true)
            .withExposedPorts(9042);

    @BeforeAll
    static void beforeAllCassandra() {
        CASSANDRA.start();
        try {
            CASSANDRA.execInContainer("cqlsh", "-e", "CREATE KEYSPACE IF NOT EXISTS ticket WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};");

            try (InputStream is = CassandraSetup.class.getResourceAsStream("/db/migration/V1__create_tables.cql")) {
                if (is != null) {
                    String cql = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                    for (String statement : cql.split(";")) {
                        String trimmed = statement.trim();
                        if (!trimmed.isEmpty()) {
                            CASSANDRA.execInContainer("cqlsh", "-k", "ticket", "-e", trimmed + ";");
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute Flyway migration script V1__create_tables.cql in Cassandra", e);
        }
    }

    @DynamicPropertySource
    static void configurePropertiesCassandra(DynamicPropertyRegistry registry) {
        registry.add("spring.cassandra.contact-points", () -> CASSANDRA.getHost() + ":" + CASSANDRA.getMappedPort(9042));
        registry.add("spring.cassandra.port", () -> CASSANDRA.getMappedPort(9042));
        registry.add("spring.cassandra.local-datacenter", CASSANDRA::getLocalDatacenter);
        registry.add("spring.cassandra.keyspace-name", () -> "ticket");
        registry.add("spring.cassandra.schema-action", () -> "CREATE_IF_NOT_EXISTS");
    }
}
