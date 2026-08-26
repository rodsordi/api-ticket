package br.com.cielo.ticket.infra.repository;

import br.com.cielo.ticket.domain.entity.User;
import br.com.cielo.ticket.domain.repository.UserRepository;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataUserRepository extends CassandraRepository<User, UUID>, UserRepository {

    @Query("SELECT * FROM users WHERE email = ?0 ALLOW FILTERING")
    @Override
    Optional<User> findByEmail(String email);
}
