package br.com.cielo.ticket.domain.entity;

import br.com.cielo.commons.entity.AuditableEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;

import java.util.UUID;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@SuperBuilder
@EqualsAndHashCode(callSuper = false, exclude = "id")
public class Auth extends AuditableEntity implements GrantedAuthority {

    private UUID id;

    @Getter(onMethod_ = @Override)
    private String authority;
}
