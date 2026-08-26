package br.com.cielo.ticket.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.Indexed;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Set;
import java.util.UUID;

import static lombok.AccessLevel.PROTECTED;

@Table("users")
@Getter
@NoArgsConstructor(access = PROTECTED)
@SuperBuilder
public class User implements UserDetails {

    @PrimaryKey
    private UUID id;

    @Column("username")
    @Getter(onMethod_ = @Override)
    private String username;

    @Column("password")
    @Getter(onMethod_ = @Override)
    private String password;

    @Column("name")
    private String name;

    @Indexed
    @Column("email")
    private String email;

    // Aggregate
    @Singular(value = "authority", ignoreNullCollections = true)
    @Column("authorities")
    private Set<Auth> authorities;

    public void update(User user) {
        if (user == null)
            return;

        if (user.name != null)
            this.name = user.name;

        if (user.email != null) {
            this.email = user.email;
            this.username = user.email;
        }
    }

    public void updatePassword(String password, PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
    }

    public void encodePassword(PasswordEncoder passwordEncoder) {
        password = passwordEncoder.encode(password);
    }
}
