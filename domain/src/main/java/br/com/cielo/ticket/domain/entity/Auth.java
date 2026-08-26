package br.com.cielo.ticket.domain.entity;

import br.com.cielo.commons.entity.AuditableEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

import static lombok.AccessLevel.PROTECTED;

@Table("auth")
@Getter
@NoArgsConstructor(access = PROTECTED)
@SuperBuilder
@EqualsAndHashCode(callSuper = false, exclude = "id")
public class Auth extends AuditableEntity implements GrantedAuthority {

    @PrimaryKey
    private UUID id;

    @Column("authority")
    @Getter(onMethod_ = @Override)
    private String authority;
}
