package br.com.cielo.ticket.application.v1.msg;

import br.com.cielo.ticket.application.v1.def.NotificationDef;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Getter(onMethod_ = @Override)
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
@Builder
public class NotificationMsg implements NotificationDef.Request {

    private UUID externalId;

    private EmailMsg email;
}
