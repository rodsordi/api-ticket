package br.com.cielo.ticket.application.mapper;

import br.com.cielo.commons.exception.FieldNotFoundException;
import br.com.cielo.commons.exception.InternalErrorException;
import br.com.cielo.ticket.domain.entity.Client;
import br.com.cielo.ticket.domain.entity.Reservation;
import br.com.cielo.ticket.application.evt.EmailEvt;
import br.com.cielo.ticket.application.evt.NotificationEvt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.replaceEach;

@Slf4j
@Component
public class NotificationEvtMapper {

    @Value("${web.ticket-web-page-url:http://localhost:8080/ticket}")
    private String ticketWebPageUrl;

    @Value("${email.estimate-customer-approval-email-subject:Ticket Reservation Subject}")
    private String estimateCustomerApprovalEmailSubject;

    @Value("${email.body-template-file-name:/estimate-customer-approval-email-message.html}")
    private String emailBodyTemplateFileName;

    public NotificationEvt convert(Reservation reservation, Client client) {
        if (reservation == null)
            return null;

        var reservationId = Optional.of(reservation)
                .map(Reservation::getId)
                .orElseThrow(() -> new FieldNotFoundException(Reservation.class, "id"));

        var recipient = Optional.ofNullable(client)
                .map(Client::getEmail)
                .orElseThrow(() -> new FieldNotFoundException(Client.class, "email"));

        var emailBody = buildEmailBody(reservation, client);
        log.info(emailBody);

        return NotificationEvt.builder()
                .externalId(reservationId)
                .email(EmailEvt.builder()
                        .recipient(recipient)
                        .subject(estimateCustomerApprovalEmailSubject)
                        .message(emailBody)
                        .build())
                .build();
    }

    private String buildEmailBody(Reservation reservation, Client client) {
        var reservationId = Optional.of(reservation)
                .map(Reservation::getId)
                .orElseThrow(() -> new FieldNotFoundException(Reservation.class, "id"));

        var customerName = Optional.ofNullable(client)
                .map(Client::getFullName)
                .orElse("Customer");

        var map = new HashMap<String, String>();
        map.put("{customerName}", customerName);
        map.put("{ticketWebPageUrl}", ticketWebPageUrl);
        map.put("{reservationId}", reservationId.toString());

        try {
            var emailBody = loadEmailFile();
            return replaceEach(emailBody, map.keySet().toArray(new String[0]), map.values().toArray(new String[0]));
        } catch (Exception e) {
            return String.format("Hello %s, reservation %s is %s.", customerName, reservationId, reservation.getStatus());
        }
    }

    private String loadEmailFile() {
        var is = ResourceLoader.class.getResourceAsStream(emailBodyTemplateFileName);
        if (is == null)
            throw new InternalErrorException(emailBodyTemplateFileName + " not found!");
        return new BufferedReader(new InputStreamReader(is))
                .lines().collect(Collectors.joining("\n"));
    }
}
