package br.com.cielo.ticket.domain.repository;

import br.com.cielo.ticket.domain.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    Optional<User> findByEmail(String email);

    User save(User user);

    Optional<User> findById(UUID id);

    void deleteById(UUID id);
}
