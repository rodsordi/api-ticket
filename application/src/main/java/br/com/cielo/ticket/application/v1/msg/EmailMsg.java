package br.com.cielo.ticket.application.v1.msg;

import br.com.cielo.ticket.application.v1.def.EmailDef;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Getter(onMethod_ = @Override)
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
@Builder
public class EmailMsg implements EmailDef.Request {

    private String recipient;
    private String subject;
    private String message;
}
