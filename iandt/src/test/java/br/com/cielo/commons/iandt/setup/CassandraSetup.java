package br.com.cielo.commons.iandt.setup;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.CassandraContainer;

public interface CassandraSetup {

    CassandraContainer<?> CASSANDRA = new CassandraContainer<>("cassandra:5.0")
            .withAccessToHost(true)
            .withExposedPorts(9042);

    @BeforeAll
    static void beforeAllCassandra() {
        CASSANDRA.start();
    }

    @DynamicPropertySource
    static void configurePropertiesCassandra(DynamicPropertyRegistry registry) {
        registry.add("spring.cassandra.contact-points", () -> CASSANDRA.getHost() + ":" + CASSANDRA.getMappedPort(9042));
        registry.add("spring.cassandra.port", () -> CASSANDRA.getMappedPort(9042));
        registry.add("spring.cassandra.local-datacenter", CASSANDRA::getLocalDatacenter);
        registry.add("spring.cassandra.keyspace-name", () -> "ticket");
        registry.add("spring.cassandra.schema-action", () -> "create_if_not_exists");
    }
}
