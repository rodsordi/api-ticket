package br.com.cielo.ticket.application.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@Configuration
@EnableCassandraRepositories(basePackages = "br.com.cielo.ticket.application.repository")
public class CassandraConfig {
}
